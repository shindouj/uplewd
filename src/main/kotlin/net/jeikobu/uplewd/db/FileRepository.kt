package net.jeikobu.uplewd.db

import net.jeikobu.uplewd.model.File
import org.springframework.data.mongodb.repository.MongoRepository

interface FileRepository : MongoRepository<File, String> {

    fun findFileById(id: String): File?
    //TODO: check if returns null or empty list
    fun findFilesByOwnerName(owner: String): List<File>?
    fun findFileByDeleteId(deleteId: String): File?
}