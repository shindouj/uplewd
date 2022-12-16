package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.model.Role
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AdminPageController {
    @Value("\${UPLEWD_INSTANCE_NAME}")
    lateinit var instanceName: String

    private val templateName = "admin/panel"

    @GetMapping("/admin")
    fun adminHandler(
        authentication: Authentication,
        model: Model
    ): String {

        val user = authentication.principal as User

        model.apply {
            addAttribute("INSTANCE_NAME", instanceName)

            addAttribute("USER_IS_ADMIN", user.roles.contains(Role.ADMIN))
        }

        return templateName
    }
}