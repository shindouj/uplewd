package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.component.TokenGenerator
import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

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
        @RequestParam("size", defaultValue = "10", required = false) sizeParam: Int
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

        val user = authentication.principal as User

        val filesPage = if (searchQuery.isEmpty()) {
            fileRepository.findFilesByOwnerNameAndDeletedIsFalse(user.username, PageRequest.of(page, size))
        } else {
            fileRepository.findFilesByOwnerNameAndOriginalFileNameContainsIgnoreCase(
                user.username, searchQuery, PageRequest.of(page, size)
            )
        }

        val basePath = if (searchQuery.isEmpty()) {
            "/$templateName?"
        } else {
            "/$templateName?searchQuery=$searchQuery&"
        }

        model.apply {
            addAttribute("INSTANCE_NAME", instanceName)
            addAttribute("HOST", host)
            addAttribute("USER_TOKEN", user.token)

            addAttribute("BASE_PATH", basePath)

            addAttribute("CURRENT_PAGE", pageParam)
            addAttribute("TOTAL_PAGES", filesPage?.totalPages);
            addAttribute("TOTAL_FILES", filesPage?.totalElements);
            addAttribute("USER_FILES", filesPage)
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