package net.jeikobu.uplewd.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path

@Service
class DeleteFileServiceImpl : DeleteFileService {

    @Value("\${UPLEWD_STORAGE_PATH}")
    lateinit var storagePath: String

    override fun deleteFile(fileName: String): Boolean = Files.deleteIfExists(Path.of(storagePath, fileName))

}