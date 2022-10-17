package net.jeikobu.uplewd.component

import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.retry
import kotlinx.coroutines.runBlocking
import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.exception.DbEntryExistsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.nio.file.Paths

@Component
class NameGenerator(private val idUtils: IdUtils, private val fileRepository: FileRepository) {

    @Value("\${UPLEWD_STORAGE_FILENAME_MAX_LENGTH:16}")
    val filenameMaxLength: Int = 16

    private fun tryGeneratingFileName(length: Int): String {
        val id = idUtils.generateId(length)
        if (fileRepository.findFileById(id) != null) {
            throw DbEntryExistsException("id $id already exists")
        }

        return id
    }

    fun generateFileName(filePath: Path): String {
        return runBlocking {
            retry(limitAttempts(3)) {
                tryGeneratingFileName(length = filenameMaxLength)
            }
        }
    }
}