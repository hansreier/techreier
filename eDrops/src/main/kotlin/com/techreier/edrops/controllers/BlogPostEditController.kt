package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogPostService
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.InitService
import com.techreier.edrops.domain.Owner
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.exceptions.KeyNotFoundException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.checkSegment
import com.techreier.edrops.util.checkStringSize
import com.techreier.edrops.util.getValidProjectLanguageCode
import com.techreier.edrops.util.msg
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping(BLOG_EDIT_DIR)
class BlogPostEditController(
    private val ctx: Context,
    private val blogPostService: BlogPostService,
    private val blogService: BlogService,
    private val blogPostRepo: BlogPostRepository,
    private val initService: InitService,
) : BaseController(ctx) {


    @GetMapping("/{segment}/{subsegment}")
    fun blogPost(
        @PathVariable segment: String,
        @PathVariable subsegment: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, false, true)

        if (blogParams.blog == null || blogParams.blog.id == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }

        val posts = blogPostRepo.findByBlogIdAndSegment(blogParams.blog.id, subsegment)
        if (posts.isEmpty()) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        val state = posts.first().state //TODO Den bare velger en. OK løsning eller ikke
        return "redirect:$BLOG_EDIT_DIR/$segment/$subsegment/$state"
    }

    @GetMapping("/{segment}/{subsegment}/{state}")
    fun blogPost(
        @PathVariable segment: String,
        @PathVariable subsegment: String,
        @PathVariable state: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, false, true)

        if (blogParams.blog == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        logger.info("Fetch blog posts: $blogParams")

        model.addAttribute("postStates", PostState.entries)
        if (subsegment == NEW_SUBSEGMENT) {
            val blogPostForm = model.getAttribute("blogPostForm") ?: BlogPostForm()
            model.addAttribute("blog", blogParams.blog)
            model.addAttribute("blogPostForm", blogPostForm)
            model.addAttribute("postHeadline", msg(ctx.messageSource, "newPost"))
        } else {
            val blogState = PostState.find(state)
            val (blogPost, blogText) = blogPostService.readBlogPost(blogParams.blog.id, subsegment, blogState) //TODO kræsjer her
            if (blogPost == null) {
                redirectAttributes.addFlashAttribute("warning", "postNotFound")
                return "redirect:/$HOME_DIR"
            }

            val datePattern = msg(ctx.messageSource, "format.datetime")
            val blogPostDto = blogPost.toDTO(timeZone(), datePattern, markdown, false, blogText)

            logger.info("getting GUI with blogPost. ${blogPost.title}") //TODO blog.subject mangler
            val contentChanged = blogPostDto.blogText?.changedString ?: ""
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
        val blogOwner = owner?.user ?:
        if (ctx.appConfig.auth)
            throw (ResponseStatusException(HttpStatus.UNAUTHORIZED, "not authorized for save action"))
        else initService.blogAdmin

        val blogOwnerId = blogOwner.id ?:  throw (ResponseStatusException(HttpStatus.UNAUTHORIZED, "No blogOwner exists"))
        val langCode = (ctx.httpSession.getAttribute("langcode") as String?) ?:
        getValidProjectLanguageCode(LocaleContextHolder.getLocale().language)
        val blogId = blogService.findId(segment, blogOwnerId, langCode ) ?:
        throw (KeyNotFoundException("blogId not found for segment $segment language $langCode"))
        val path = request.servletPath
        val blogPostId = blogPostService.findId(subsegment, blogId, PostState.find(state))

        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blogPost: path=$path action=$action blogid=$blogId blogPostId=$blogPostId")
        if (action == "save" || action == "create" || action == "copy" || action == "blog" )  {

            if (checkSegment(form.segment, "segment", bindingResult)) {
                if (blogPostService.duplicate(form.segment, blogId, form.state, blogPostId)) {
                    bindingResult.rejectValue("segment", "error.duplicate", form.segment)
                }
            }

            checkStringSize(form.title, MAX_TITLE_SIZE, "title", bindingResult, 1)
            form.title = form.title.replaceFirstChar { it.uppercaseChar() }
            checkStringSize(form.summary, MAX_SUMMARY_SIZE, "summary", bindingResult)

            if (bindingResult.hasErrors()) {
                bindingResult.reject("error.savePost")
                prepare(model, request, response, segment, changed)
                return "blogPostEdit"
            }

            try {
                    blogPostService.save(blogId, blogPostId, form, now())
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
                prepare(model, request, response, subsegment, changed)
                return "blogPostEdit"
            }
        }

        if (action == "delete") {
            try {
                blogPostService.delete(blogId, blogPostId, form)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, subsegment, changed)
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
            prepare(model, request, response, subsegment, changed)
            return "blogPostEdit"
        }

        if (action == "help") {
            model.addAttribute("help", "h")
            prepare(model, request, response, subsegment, changed)
            return "blogPostEdit"
        }

        // This should never really occur
        logger.error("Illegal action: $action")
        bindingResult.reject("error.illegalAction")
        prepare(model, request, response, subsegment, changed)
        return "blogPostEdit"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        segment: String,
        changed: String,
    ) {
        val blogParams = fetchBlogParams(model, request, response, segment)
        logger.info("Prepare allBlogPosts Fetch blog posts with: $blogParams")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("postPath", "$BLOG_EDIT_DIR/$segment/")
        model.addAttribute("changed", changed)
        model.addAttribute("postStates", PostState.entries)
        logger.info("prepared)")
    }

}
