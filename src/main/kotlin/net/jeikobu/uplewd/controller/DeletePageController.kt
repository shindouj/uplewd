package net.jeikobu.uplewd.controller

import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.exception.UploadedFileNotFoundException
import net.jeikobu.uplewd.service.DeleteFileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.tinylog.kotlin.Logger
import java.io.IOException
import java.time.Instant

@RestController
class DeletePageController(val fileRepository: FileRepository, val deleteFileService: DeleteFileService) {

    @GetMapping("/delete/{deleteId}")
    fun deleteHandler(@PathVariable("deleteId") deleteId: String) {
        Logger.info { "Delete: Starting deletion process [deleteId: $deleteId]" }

        val file = fileRepository.findFileByDeleteIdAndDeletedIsFalse(deleteId)
            ?: throw UploadedFileNotFoundException("File with deletion ID [$deleteId] not found.")
        Logger.debug { "Delete: File suitable for deletion [fileName: ${file.fileName}, deleteId: $deleteId]" }

        if (!deleteFileService.deleteFile(file.fileName)) {
            throw IOException("Failed to delete file ${file.fileName} (Deletion ID: ${file.deleteId})")
        }
        Logger.info { "Delete: File deleted from filesystem [fileName: ${file.fileName}, deleteId: $deleteId]" }

        file.apply {
            deleted = true
            deletionTime = Instant.now()
        }

        fileRepository.save(file)
        Logger.info { "Delete: Changes to file saved to database [fileName: ${file.fileName}, deleteId: $deleteId]" }
    }

}