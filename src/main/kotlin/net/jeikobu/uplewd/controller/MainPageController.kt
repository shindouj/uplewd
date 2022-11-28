package net.jeikobu.uplewd.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainPageController {
    @Value("\${UPLEWD_INSTANCE_NAME}")
    lateinit var instanceName: String

    @Value("\${UPLEWD_GLOBAL_UPLOAD_LIMIT}")
    lateinit var uploadLimit: String

    private val templateName = "index"

    @GetMapping("/")
    fun mainPage(model: Model): String {
        model.apply {
            addAttribute("INSTANCE_NAME", instanceName)
            addAttribute("MAX_UPLOAD", uploadLimit)
        }

        return templateName
    }

}