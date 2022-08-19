package net.jeikobu.uplewd.component

import net.jeikobu.uplewd.db.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UplewdUserDetailsService @Autowired constructor(
    val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val response = userRepository.findUserByUsername(username)
        return response ?: throw UsernameNotFoundException(username)
    }
}