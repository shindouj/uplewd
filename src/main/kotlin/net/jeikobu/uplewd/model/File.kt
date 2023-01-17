package net.jeikobu.uplewd.model

import org.springframework.data.annotation.Id
import java.time.Instant

data class File(
    @Id
    val id: String,
    val fileName: String,
    val originalFileName: String,
    val ownerName: String,
    val uploadDate: Instant = Instant.now(),
    val deleteId: String,
    var deleted: Boolean = false,
    var expirationTime: Instant? = null,
    var deletionTime: Instant? = null
)