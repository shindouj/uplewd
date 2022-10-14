package net.jeikobu.uplewd.advice

import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MultipartException
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.tinylog.kotlin.Logger

@RestControllerAdvice
class ControllerAdvice {
    @Value("\${UPLEWD_GLOBAL_UPLOAD_LIMIT}")
    lateinit var uploadLimit: String

    @ExceptionHandler(value = [MultipartException::class])
    fun uploadSizeExceptionHandler(authentication: Authentication, e: MultipartException, redirectAttributes: RedirectAttributes): ResponseEntity<Map<String, String>> {
        Logger.debug {
            val user = authentication.principal as User
            "Upload limit [$uploadLimit] exceeded by ${user.username}"
        }

        val responseMap = mapOf(
            "error" to "File exceeds the global upload limit size [$uploadLimit]!"
        )
        return ResponseEntity(responseMap, HttpStatus.PAYLOAD_TOO_LARGE)
    }
}