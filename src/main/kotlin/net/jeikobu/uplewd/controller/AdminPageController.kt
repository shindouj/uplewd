package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.Role
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder

@Controller
class AdminPageController constructor(
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
) {
    @Value("\${UPLEWD_INSTANCE_NAME}")
    lateinit var instanceName: String

    private val usersTemplateName = "admin/users"
    private val filesTemplateName = "admin/files"

    @GetMapping("/admin/users")
    fun adminUsersHandler(
        authentication: Authentication,
        model: Model,
        @RequestParam("searchQuery", defaultValue = "", required = false) searchQuery: String,
        @RequestParam("page", defaultValue = "1", required = false) pageParam: Int,
        @RequestParam("size", defaultValue = "10", required = false) sizeParam: Int,
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
            .path("/$usersTemplateName")

        if (page != 0) {
            uriBuilder.queryParam("page", page + 1)
        }
        if (size != 10) {
            uriBuilder.queryParam("size", size)
        }
        if (searchQuery.isNotEmpty()) {
            uriBuilder.queryParam("searchQuery", searchQuery)
        }

        val user = authentication.principal as User

        val usersPage = if (searchQuery.isEmpty()) {
            userRepository.findAll(PageRequest.of(page, size))
        } else {
            userRepository.findByUsernameContainsIgnoreCase(searchQuery, PageRequest.of(page, size))
        }

        model.apply {
            addAttribute("INSTANCE_NAME", instanceName)
            addAttribute("USER_TOKEN", user.token)

            addAttribute("URI_BUILDER", uriBuilder)

            addAttribute("CURRENT_PAGE", pageParam)
            addAttribute("TOTAL_PAGES", usersPage?.totalPages);
            addAttribute("TOTAL_USERS", usersPage?.totalElements);
            addAttribute("USERS", usersPage)

            addAttribute("USER_IS_ADMIN", user.roles.contains(Role.ADMIN))
        }

        return usersTemplateName
    }

    @GetMapping("/admin/files")
    fun adminFilesHandler(
        authentication: Authentication,
        model: Model,
        @RequestParam("searchQuery", defaultValue = "", required = false) searchQuery: String,
        @RequestParam("page", defaultValue = "1", required = false) pageParam: Int,
        @RequestParam("size", defaultValue = "10", required = false) sizeParam: Int,
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
            .path("/$filesTemplateName")

        if (page != 0) {
            uriBuilder.queryParam("page", page + 1)
        }
        if (size != 10) {
            uriBuilder.queryParam("size", size)
        }
        if (searchQuery.isNotEmpty()) {
            uriBuilder.queryParam("searchQuery", searchQuery)
        }

        val user = authentication.principal as User

        val filesPage = if (searchQuery.isEmpty()) {
            fileRepository.findAll(PageRequest.of(page, size))
        } else {
            fileRepository.findFilesByOwnerNameContainsIgnoreCaseOrFileNameContainsIgnoreCaseOrOriginalFileNameContainsIgnoreCase(
                searchQuery, searchQuery, searchQuery,
                PageRequest.of(page, size)
            )
        }

        model.apply {
            addAttribute("INSTANCE_NAME", instanceName)

            addAttribute("URI_BUILDER", uriBuilder)

            addAttribute("CURRENT_PAGE", pageParam)
            addAttribute("TOTAL_PAGES", filesPage?.totalPages);
            addAttribute("TOTAL_USERS", filesPage?.totalElements);
            addAttribute("FILES", filesPage)

            addAttribute("USER_IS_ADMIN", user.roles.contains(Role.ADMIN))
        }

        return filesTemplateName
    }

}