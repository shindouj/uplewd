package net.jeikobu.uplewd.component

import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.retry
import kotlinx.coroutines.runBlocking
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.exception.DbEntryExistsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class TokenGenerator(private val idUtils: IdUtils, private val userRepository: UserRepository) {

    @Value("\${UPLEWD_USER_TOKEN_MAX_LENGTH:128}")
    val tokenMaxLength: Int = 128

    private fun tryGeneratingToken(length: Int): String {
        val token = idUtils.generateId(length)
        if (userRepository.findUserByToken(token) != null) {
            throw DbEntryExistsException("token $token already exists")
        }

        return token
    }

    fun generateToken(): String {
        return runBlocking {
            retry(limitAttempts(3)) {
                tryGeneratingToken(length = tokenMaxLength)
            }
        }
    }
}