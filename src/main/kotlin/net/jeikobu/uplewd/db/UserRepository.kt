package net.jeikobu.uplewd.db

import net.jeikobu.uplewd.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByUsernameContainsIgnoreCase(username: String, pageRequest: Pageable): Page<User>?
    fun findUserByUsername(username: String): User?
    fun findUserByToken(token: String): User?
}