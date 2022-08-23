package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.exception.UploadedFileNotFoundException
import net.jeikobu.uplewd.service.DeleteFileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.time.Instant

@RestController
class DeletePageController(val fileRepository: FileRepository, val deleteFileService: DeleteFileService) {

    @GetMapping("/delete/{deleteId}")
    fun deleteHandler(@PathVariable("deleteId") deleteId: String) {
        val file = fileRepository.findFileByDeleteIdAndDeletedIsFalse(deleteId)
            ?: throw UploadedFileNotFoundException("File with deletion ID [$deleteId] not found.")

        if (!deleteFileService.deleteFile(file.fileName)) {
            throw IOException("Failed to delete file ${file.fileName} (Deletion ID: ${file.deleteId})")
        }

        file.apply {
            deleted = true
            deletionTime = Instant.now()
        }

        fileRepository.save(file)
    }

}