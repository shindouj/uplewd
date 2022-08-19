package net.jeikobu.uplewd.db

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.userdetails.User

interface UserRepository : MongoRepository<User, String> {
    fun findUserByUsername(username: String): User?
}