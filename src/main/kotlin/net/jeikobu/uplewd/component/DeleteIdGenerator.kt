package net.jeikobu.uplewd.component

import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.retry
import kotlinx.coroutines.runBlocking
import net.jeikobu.uplewd.db.FileRepository
import net.jeikobu.uplewd.exception.DbEntryExistsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DeleteIdGenerator(val idUtils: IdUtils, val fileRepository: FileRepository) {

    @Value("\${UPLEWD_DELETE_ID_MAX_LENGTH:64}")
    val deleteIdMaxLength: Int = 64

    private fun tryGeneratingDeleteId(length: Int): String {
        val id = idUtils.generateId(length)
        if (fileRepository.findFileByDeleteId(id) != null) {
            throw DbEntryExistsException("deleteId $id already exists")
        }

        return id
    }

    fun generateDeleteId() : String {
        return runBlocking {
            retry(limitAttempts(3)) {
                tryGeneratingDeleteId(length = deleteIdMaxLength)
            }
        }
    }


}