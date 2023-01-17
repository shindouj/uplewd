package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.component.DeleteIdGenerator
import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.exception.DbEntryExistsException
import net.jeikobu.uplewd.model.File
import net.jeikobu.uplewd.model.RetentionPeriod
import net.jeikobu.uplewd.model.User
import net.jeikobu.uplewd.service.UploadFileService
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.tinylog.kotlin.Logger
import java.time.Instant

@RestController
class UploadPageController @Autowired constructor(
    val uploadFileService: UploadFileService,
    val fileRepository: FileRepository,
    val deleteIdGenerator: DeleteIdGenerator
) {

    @Value("\${UPLEWD_HOST}")
    lateinit var host: String

    @Value("\${UPLEWD_FILE_HOST}")
    lateinit var fileHost: String

    @PostMapping("/upload")
    fun uploadHandler(authentication: Authentication, @RequestParam("file") file: MultipartFile): Map<String, String> {
        val user = authentication.principal as User
        Logger.info { "Upload: Processing upload [User: ${user.username}]" }

        val (savedFileNameWithoutExtension, savedFileName) = uploadFileService.saveFile(file)
        Logger.debug { "Upload: File saved to filesystem as $savedFileName [User: ${user.username}]" }

        val savedFile = fileRepository.save(
            File(
                id = savedFileNameWithoutExtension,
                fileName = savedFileName,
                originalFileName = file.originalFilename ?: "",
                ownerName = user.username,
                deleteId = deleteIdGenerator.generateDeleteId(),
                expirationTime = if (user.userSettings.retentionPeriod == RetentionPeriod.INF) {
                    null
                } else {
                    Instant.now().plusSeconds(user.userSettings.retentionPeriod.seconds)
                }
            )
        )
        Logger.debug { "Upload: $savedFileName saved to database [User: ${user.username}]" }
        Logger.info { "Upload: Successfully finished upload [User: ${user.username}]" }

        return mapOf(
            "id" to savedFileNameWithoutExtension,
            "url" to "$fileHost/${savedFile.fileName}",
            "deleteId" to savedFile.deleteId,
            "deletionUrl" to "$host/delete/${savedFile.deleteId}"
        )
    }
}