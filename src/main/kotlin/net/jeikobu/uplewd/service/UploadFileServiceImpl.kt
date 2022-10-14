package net.jeikobu.uplewd.service

import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.retry
import kotlinx.coroutines.runBlocking
import net.jeikobu.uplewd.component.NameGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

@Service
class UploadFileServiceImpl constructor(
    val nameGenerator: NameGenerator
) : UploadFileService {

    @Value("\${UPLEWD_STORAGE_PATH}")
    lateinit var storagePath: String

    override fun saveFile(file: MultipartFile): Pair<String, String> {
        val targetFilePath = Path(storagePath)
        if (!targetFilePath.exists()) {
            throw FileNotFoundException("storage path does not exist")
        }

        val targetFileName = nameGenerator.generateFileName(targetFilePath)
        val extension = File(file.originalFilename ?: "").extension
        val targetNameWithExtension = if (extension.isNotEmpty()) {
            "$targetFileName.$extension"
        } else {
            targetFileName
        }

        file.transferTo(targetFilePath.resolve(Paths.get(targetNameWithExtension)).toFile())

        return targetFileName to targetNameWithExtension
    }
}