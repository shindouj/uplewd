package net.jeikobu.uplewd.component

import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.service.DeleteFileService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.tinylog.kotlin.Logger
import java.io.IOException
import java.time.Instant

@Component
class FileCleanupTask(
    private val fileRepository: FileRepository,
    private val deleteFileService: DeleteFileService
) {

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    fun removeStaleFiles(){
        val staleFiles = fileRepository.findFilesByExpirationTimeNotNullAndExpirationTimeBefore(Instant.now()) ?: emptyList()
        for(file in staleFiles){
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

}