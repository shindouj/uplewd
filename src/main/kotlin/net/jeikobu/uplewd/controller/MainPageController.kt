package net.jeikobu.uplewd.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainPageController {
    private val templateName = "index"

    @GetMapping("/")
    fun mainPage(): String {
        return templateName
    }
}