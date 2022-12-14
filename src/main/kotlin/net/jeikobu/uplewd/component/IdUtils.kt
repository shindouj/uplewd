package net.jeikobu.uplewd.component

import org.springframework.stereotype.Component

@Component
class IdUtils {

    private val characterPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generateId(length: Int) = (1..length)
        .map { characterPool.random() }
        .joinToString("")

}