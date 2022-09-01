package net.jeikobu.uplewd.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "The requested deletion ID has not been found.")
class UploadedFileNotFoundException(message: String) : Exception(message)