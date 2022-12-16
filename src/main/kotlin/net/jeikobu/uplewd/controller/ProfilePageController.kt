package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.component.TokenGenerator
import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.Role
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest

@Controller
class ProfilePageController @Autowired constructor(
    private val fileRepository: FileRepository,
    private val tokenGenerator: TokenGenerator,
    private val userRepository: UserRepository
) {
    @Value("\${UPLEWD_INSTANCE_NAME}")
    lateinit var instanceName: String

    @Value("\${UPLEWD_HOST}")
    lateinit var host: String

    private val templateName = "profile"

    @GetMapping("/profile")
    fun userPanelPage(
        authentication: Authentication,
        model: Model,
        @RequestParam("searchQuery", defaultValue = "", required = false) searchQuery: String,
        @RequestParam("page", defaultValue = "1", required = false) pageParam: Int,
        @RequestParam("size", defaultValue = "10", required = false) sizeParam: Int,
        request: HttpServletRequest
    ): String {

        //TODO: sanitize search query?
        val page = if (pageParam >= 1) {
            pageParam - 1
        } else {
            0
        }

        val size = if (sizeParam <= 0) {
            1
        } else {
            sizeParam
        }

        val uriBuilder = UriComponentsBuilder.fromUriString("")
            .path("/$templateName")

        if (page != 0) {
            uriBuilder.queryParam("page", page + 1)
        }
        if (size != 10) {
            uriBuilder.queryParam("size", size)
        }
        if(searchQuery.isNotEmpty()){
            uriBuilder.queryParam("searchQuery", searchQuery)
        }

        val user = authentication.principal as User

        val filesPage = if (searchQuery.isEmpty()) {
            fileRepository.findFilesByOwnerNameAndDeletedIsFalse(user.username, PageRequest.of(page, size))
        } else {
            fileRepository.findFilesByOwnerNameAndOriginalFileNameContainsIgnoreCase(
                user.username, searchQuery, PageRequest.of(page, size)
            )
        }

        model.apply {
            addAttribute("INSTANCE_NAME", instanceName)
            addAttribute("HOST", host)
            addAttribute("USER_TOKEN", user.token)

            addAttribute("URI_BUILDER", uriBuilder)

            addAttribute("CURRENT_PAGE", pageParam)
            addAttribute("TOTAL_PAGES", filesPage?.totalPages);
            addAttribute("TOTAL_FILES", filesPage?.totalElements);
            addAttribute("USER_FILES", filesPage)

            addAttribute("USER_IS_ADMIN", user.roles.contains(Role.ADMIN))
        }

        return templateName
    }

    @ResponseBody
    @PatchMapping("/profile/refreshToken")
    fun refreshToken(authentication: Authentication): Map<String, String> {
        val user = authentication.principal as User
        user.token = tokenGenerator.generateToken()
        userRepository.save(user)

        return mapOf(
            "token" to user.token
        )
    }
}