package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.component.DeleteIdGenerator
import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.model.File
import net.jeikobu.uplewd.model.User
import net.jeikobu.uplewd.service.UploadFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class UploadPageController @Autowired constructor(
    val uploadFileService: UploadFileService,
    val fileRepository: FileRepository,
    val deleteIdGenerator: DeleteIdGenerator
) {

    @Value("\${UPLEWD_HOST}")
    lateinit var host: String
    @PostMapping("/upload")
    fun uploadHandler(authentication: Authentication, @RequestParam("file") file: MultipartFile): Map<String,String> {
        val user = authentication.principal as User

        val (savedFileNameWithoutExtension, savedFileName) = uploadFileService.saveFile(file)

        val savedFile = fileRepository.save(File(
            id = savedFileNameWithoutExtension,
            fileName = savedFileName,
            ownerName = user.username,
            deleteId = deleteIdGenerator.generateDeleteId()
        ))

        return mapOf(
            "id" to savedFileNameWithoutExtension,
            "url" to "$host/${savedFile.fileName}",
            "deleteId" to savedFile.deleteId,
            "deletionUrl" to "$host/delete/${savedFile.deleteId}"
        )
    }

}