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
import com.techreier.edrops.repository.projections.toDTO
import com.techreier.edrops.util.Markdown
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

    @GetMapping("/{segment}/{subsegment}/{state}")
    fun blogPost(
        @PathVariable segment: String,
        @PathVariable subsegment: String,
        @PathVariable state: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        @AuthenticationPrincipal owner: Owner?,
    ): String {
        authorize(owner)
        logger.info("ReierAsk reading")
        val blogParams = fetchBlogParams(model, request, response, segment, false, true)
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
        changed: String,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        @AuthenticationPrincipal owner: Owner?,
    ): String {
        val blogPrincipal = getAuthorizedBlogPrincipal(owner, segment)
        val blogId = blogPrincipal.blogId
            ?: throw (BlogNotFoundException("blogId not found for segment $segment language $blogPrincipal.langCode"))
        val path = request.servletPath

        val state = PostState.find(state, false)
        val blogPostIds = blogPostService.findIds(subsegment, blogId, state)

        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blogPost: path=$path action=$action blogid=$blogId blogPostIds=$blogPostIds")
        if ((action == "blog") && (blogPostIds.size > 1 )) {
            return "redirect:$BLOG_EDIT_DIR/$segment"
        }
        if (action == "save" || action == "create" || action == "copy" || action == "blog") {
            if (blogPostIds.size > 1)
                bindingResult.rejectValue("segment", "error.duplicate", form.segment)

            checkSegment(form.segment, "segment", bindingResult)
            checkStringSize(form.title, MAX_TITLE_SIZE, "title", bindingResult, 1)
            form.title = form.title.replaceFirstChar { it.uppercaseChar() }
            checkStringSize(form.summary, MAX_SUMMARY_SIZE, "summary", bindingResult)

            if (bindingResult.hasErrors()) {
                bindingResult.reject("error.savePost")
                prepare(model, request, response, segment, changed, blogPostIds)
                return "blogPostEdit"
            }
            try {
                blogPostService.save(blogId, blogPostIds.firstOrNull(), form, now())
                if (action == "copy") {
                    form.state = PostState.DRAFT
                    form.postLock = true
                    redirectAttributes.addFlashAttribute("blogPostForm", form)
                }
                if (action == "blog") {
                    return "redirect:$BLOG_EDIT_DIR/$segment"
                }
                val newPath = "$BLOG_EDIT_DIR/$segment" +
                        if (action == "save")
                            "/${form.segment}/${form.state.lower()}"
                        else
                            "/$NEW_SUBSEGMENT/${PostState.IDEA.lower()}"
                return "redirect:$newPath"
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException, is ParentBlogException -> handleRecoverableError(e, "dbSave", bindingResult)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed, blogPostIds)
                return "blogPostEdit"
            }
        }

        if (action == "delete") {
            try {
                blogPostService.delete(blogId, blogPostIds, form)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, segment, changed, blogPostIds)
                return "blogPostEdit"
            }
            return "redirect:$BLOG_EDIT_DIR/$segment"
        }

        if (action == "view") {
            if ((form.focus.isNotEmpty() || form.preview.isEmpty())) {
                if (!form.summary.isBlank() && (form.focus.isEmpty() || form.focus == "s")) {
                    val summary = Markdown().toHtml(form.summary)
                    model.addAttribute("summary", summary)
                    form.preview = "x"
                }

                if (!form.content.isBlank() && (form.focus.isEmpty() || form.focus == "c")) {
                    val content = Markdown().toHtml(form.content)
                    model.addAttribute("content", content)
                    form.preview = "x"
                }
            } else {
                form.preview = ""
            }
            prepare(model, request, response, segment, changed, blogPostIds)
            return "blogPostEdit"
        }

        if (action == "help") {
            model.addAttribute("help", "h")
            prepare(model, request, response, segment, changed, blogPostIds)
            return "blogPostEdit"
        }

        // This should never really occur
        logger.error("Illegal action: $action")
        bindingResult.reject("error.illegalAction")
        prepare(model, request, response, segment, changed, blogPostIds)
        return "blogPostEdit"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        segment: String,
        changed: String,
        blogPostIds: List<Long>
    ) {
        val blogParams = fetchBlogParams(model, request, response, segment)
        logger.info("Prepare allBlogPosts Fetch blog posts with: ${blogParams}")

        blogParams.blog ?: throw BlogNotFoundException("Blog with segment $segment not found")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("blogPath", "$BLOG_EDIT_DIR/$segment/")
        model.addAttribute("changed", changed)
        model.addAttribute("postStates", PostState.entries)
        if (blogPostIds.size > 1) {
            model.addAttribute("duplicates", blogPostIds)
        }
        logger.info("prepared)")
    }

}
