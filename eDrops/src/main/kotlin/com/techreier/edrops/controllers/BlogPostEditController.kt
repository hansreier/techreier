package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.NEW_SUBSEGMENT
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogPostService
import com.techreier.edrops.domain.Owner
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.exceptions.BlogNotFoundException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.exceptions.PostNotFoundException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.projections.IBlogPostSummary
import com.techreier.edrops.repository.projections.toDTO
import com.techreier.edrops.util.checkSegment
import com.techreier.edrops.util.checkStringSize
import com.techreier.edrops.util.msg
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataAccessException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping(BLOG_EDIT_DIR)
class BlogPostEditController(
    private val ctx: Context,
    private val blogPostService: BlogPostService,
) : BaseController(ctx) {

    @GetMapping(
        "/{segment}/{subsegment}/{state}",
        "/{segment}/{subsegment}/",
        "/{segment}/{subsegment}",
        "/{segment}/"
    )
    fun blogPost(
        @PathVariable segment: String,
        @PathVariable subsegment: String = "",
        @PathVariable state: String = "",
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        @AuthenticationPrincipal owner: Owner?,
        @RequestParam lang: String
    ): String {
        authorize(owner)
        val blogParams = fetchBlogParams(model, request, response, segment, false, true, lang)
        if (blogParams.blog == null)
            throw BlogNotFoundException("blog with segment: $segment is not found")
        logger.info("Fetch blog posts: $blogParams")

        model.addAttribute("postStates", PostState.entries)
        if (subsegment == NEW_SUBSEGMENT) {
            val blogPostForm = model.getAttribute("blogPostForm") ?: BlogPostForm()
            model.addAttribute("blog", blogParams.blog)
            model.addAttribute("blogPostForm", blogPostForm)
            model.addAttribute("postHeadline", msg(ctx.messageSource, "newPost"))
        } else {
            val postState = PostState.find(state, false)
            val (blogPost, blogText, duplicates) = blogPostService.readBlogPost(
                blogParams.blog.id,
                subsegment,
                postState
            )
            blogPost
                ?: throw PostNotFoundException("blogpost with segment: $segment subsegment $subsegment is not found")

            val datePattern = msg(ctx.messageSource, "format.datetime")
            val blogPostDto = blogPost.toDTO(timeZone(), datePattern, markdown, false, blogText)
            logger.info("getting GUI with blogPost. ${blogPost.title}")
            val contentChanged = blogPostDto.blogText?.changedString ?: ""
            if (duplicates.isNotEmpty()) {
                model.addAttribute("duplicates", duplicates)
            }
            model.addAttribute("blog", blogParams.blog)
            model.addAttribute("postHeadline", blogPostDto.title)
            model.addAttribute("created", (blogPostDto.createdString))
            model.addAttribute("changed", (blogPostDto.changedString))
            model.addAttribute("contentChanged", contentChanged)
            model.addAttribute("blogPostForm", blogPostDto.toForm())
            model.addAttribute("postId", blogPost.id)
        }
        return "blogPostEdit"
    }

    @PostMapping(value = ["/{segment}/{subsegment}/{state}"])
    fun action(
        redirectAttributes: RedirectAttributes,
        @ModelAttribute form: BlogPostForm,
        @PathVariable segment: String,
        @PathVariable subsegment: String,
        @PathVariable state: String,
        action: String,
        created: String,
        changed: String,
        @RequestParam blogLangcode: String,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        @AuthenticationPrincipal owner: Owner?,
    ): String {
        val blogPrincipal = authorize(owner, segment, blogLangcode)
        val blogId = blogPrincipal.blogId
            ?: throw (BlogNotFoundException("blogId not found for segment $segment language $blogPrincipal.langCode"))

        val state = PostState.find(state, false)
        val blogPostSummaries = blogPostService.findSummaries(subsegment, blogId, state)

        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blogPost: path=${request.servletPath} action=$action blogid=$blogId blogPostIds=$blogPostSummaries")
        if (action == "back")  {
            return "redirect:$BLOG_EDIT_DIR/$segment?lang=$blogLangcode"
        }
        if (action == "save" || action == "create" || action == "copy" ) {
            if (blogPostSummaries.size > 1)
                bindingResult.rejectValue("segment", "error.duplicate", form.segment)

            checkStringSize(form.title, MAX_TITLE_SIZE, "title", bindingResult, 1)
            form.title = form.title.replaceFirstChar { it.uppercaseChar() }
            checkStringSize(form.summary, MAX_SUMMARY_SIZE, "summary", bindingResult)
            if (checkSegment(form.segment, "segment", bindingResult)) {
                if (blogPostService.duplicate(form.segment, blogId, form.state, blogPostSummaries.firstOrNull()?.id)) {
                    bindingResult.rejectValue("segment", "error.duplicate", form.segment)
                }
            }

            if (bindingResult.hasErrors()) {
                bindingResult.reject("error.savePost")
                prepare(model, request, response, segment, created,changed, blogPostSummaries)
                return "blogPostEdit"
            }
            try {
                val blogPostId = blogPostSummaries.firstOrNull()?.id
                val timestamp = if (form.bumped || blogPostId == null) { now() } else { blogPostSummaries.first().changed }
                blogPostService.save(blogId, blogPostId, form, timestamp)
                if (action == "copy") {
                    form.state = PostState.IDEA
                    form.postLock = true
                    redirectAttributes.addFlashAttribute("blogPostForm", form)
                }
                val newPath = "$BLOG_EDIT_DIR/$segment" +
                        if (action == "save")
                            "/${form.segment}/${form.state.lower()}"
                        else
                            "/$NEW_SUBSEGMENT/${PostState.IDEA.lower()}"
                return "redirect:$newPath?lang=$blogLangcode"
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException, is ParentBlogException -> handleRecoverableError(e, "dbSave", bindingResult)
                    else -> throw e
                }
                prepare(model, request, response, segment, created,changed, blogPostSummaries)
                return "blogPostEdit"
            }
        }

        if (action == "delete") {
            try {
                val blogIdList = blogPostSummaries.map {
                    it.id
                }
                blogPostService.delete(blogId, blogIdList)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, segment, created,changed, blogPostSummaries)
                return "blogPostEdit"
            }
            return "redirect:$BLOG_EDIT_DIR/$segment?lang=$blogLangcode"
        }

        if (action == "view") {
            if ((form.focus.isNotEmpty() || form.preview.isEmpty())) {
                if (!form.summary.isBlank() && (form.focus.isEmpty() || form.focus == "s")) {
                    val summary = ctx.markdown.toHtml(form.summary)
                    model.addAttribute("summary", summary)
                    form.preview = "x"
                }

                if (!form.content.isBlank() && (form.focus.isEmpty() || form.focus == "c")) {
                    val content = ctx.markdown.toHtml(form.content)
                    model.addAttribute("content", content)
                    form.preview = "x"
                }
            } else {
                form.preview = ""
            }
            prepare(model, request, response, segment, created, changed, blogPostSummaries)
            return "blogPostEdit"
        }

        if (action == "help") {
            model.addAttribute("help", "h")
            prepare(model, request, response, segment, created, changed, blogPostSummaries)
            return "blogPostEdit"
        }

        // This should never really occur
        logger.error("Illegal action: $action")
        bindingResult.reject("error.illegalAction")
        prepare(model, request, response, segment, created, changed,  blogPostSummaries)
        return "blogPostEdit"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        segment: String,
        created: String,
        changed: String,
        blogPostSummaries: List<IBlogPostSummary>,
    ) {
        val blogParams = fetchBlogParams(model, request, response, segment, false, true)
        logger.info("Prepare allBlogPosts Fetch blog posts with: ${blogParams}")
        blogParams.blog ?: throw BlogNotFoundException("Blog with segment $segment not found")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("blogPath", "$BLOG_EDIT_DIR/$segment/")
        model.addAttribute("changed", changed)
        model.addAttribute("created", created)
        model.addAttribute("postStates", PostState.entries)
        if (blogPostSummaries.size > 1) {
            model.addAttribute("duplicates", blogPostSummaries.map { it.id })
        } else {
            model.addAttribute("postId", blogPostSummaries.firstOrNull()?.id)
        }
        logger.info("prepared)")
    }

}
