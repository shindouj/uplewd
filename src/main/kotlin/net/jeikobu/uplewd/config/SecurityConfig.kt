package net.jeikobu.uplewd.config

import net.jeikobu.uplewd.component.UplewdUserDetailsService
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.Role
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
class SecurityConfig @Autowired constructor(
    val userDetailsService: UplewdUserDetailsService
) {

    @Bean
    fun userDetailsService(): UserDetailsService {
        return userDetailsService
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun daoAuthenticationProvider(): AuthenticationProvider = DaoAuthenticationProvider().apply {
        setPasswordEncoder(passwordEncoder())
        setUserDetailsService(userDetailsService)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize(anyRequest, authenticated)
                authorize("/admin/**", hasRole(Role.ADMIN.roleName))
                authorize("/delete/**", anonymous)
            }
            formLogin { }
            httpBasic { }
            csrf { disable() } //TODO: remove this and create proper fix - gives 403 on file upload without it
        }
        return http.build()
    }

    @Bean
    fun addStuff(repo: UserRepository, penc: PasswordEncoder): CommandLineRunner = CommandLineRunner {
        val u1 = repo.findUserByUsername("someUser")
        if (u1 == null) {
            val pass = penc.encode("password")
            val user = User(username = "someUser", password = pass, roles = mutableListOf(Role.ADMIN))

            repo.save(user)
        }
    }
}