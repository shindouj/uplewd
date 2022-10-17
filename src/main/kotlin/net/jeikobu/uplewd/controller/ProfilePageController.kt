package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ProfilePageController @Autowired constructor(
    private val fileRepository: FileRepository
) {
    @Value("\${UPLEWD_INSTANCE_NAME}")
    lateinit var instanceName: String

    @Value("\${UPLEWD_HOST}")
    lateinit var host: String

    private val templateName = "userPanel"

    @GetMapping("/userPanel")
    fun userPanelPage(authentication: Authentication, model: Model): String {
        val user = authentication.principal as User
        val files = fileRepository.findFilesByOwnerNameAndDeletedIsFalse(user.username)

        model.apply {
            addAttribute("INSTANCE_NAME", instanceName)
            addAttribute("HOST", host)
            addAttribute("USER_TOKEN", user.token)
            addAttribute("USER_FILES", files)
        }

        return templateName
    }
}