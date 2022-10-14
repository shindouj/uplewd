package net.jeikobu.uplewd.db

import net.jeikobu.uplewd.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findUserByUsername(username: String): User?
    fun findUserByToken(token: String): User?
}