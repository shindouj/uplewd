package net.jeikobu.uplewd.db

import net.jeikobu.uplewd.model.File
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface FileRepository : MongoRepository<File, String> {

    fun findFileById(id: String): File?

    //TODO: check if returns null or empty list
    fun findFilesByOwnerName(owner: String): List<File>?
    fun findFilesByOwnerNameAndDeletedIsFalse(owner: String): List<File>?
    fun findFilesByOwnerNameAndDeletedIsFalse(owner: String, pageRequest: Pageable): Page<File>?
    fun findFilesByOwnerNameAndOriginalFileNameContainsIgnoreCase(owner: String, originalFileName: String, pageRequest: Pageable): Page<File>?
    fun findFileByDeleteId(deleteId: String): File?
    fun findFileByDeleteIdAndDeletedIsFalse(deleteId: String): File?
}