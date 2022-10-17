package net.jeikobu.uplewd.config

import net.jeikobu.uplewd.component.UplewdUserDetailsService
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.Role
import net.jeikobu.uplewd.model.User
import net.jeikobu.uplewd.security.BearerAuthorizationMatcher
import net.jeikobu.uplewd.security.BearerTokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher

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
    fun getTokenFilter(userRepository: UserRepository): BearerTokenFilter {
        return BearerTokenFilter(userRepository)
    }

    @Bean
    fun filterChain(http: HttpSecurity, bearerTokenFilter: BearerTokenFilter): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize("/delete/**", permitAll)
                authorize("/admin/**", hasRole(Role.ADMIN.roleName))
                authorize(anyRequest, authenticated)
            }
            formLogin { }
            httpBasic { }
            csrf {
                requireCsrfProtectionMatcher = NegatedRequestMatcher(
                    OrRequestMatcher(
                        BearerAuthorizationMatcher(),
                        NegatedRequestMatcher(CsrfFilter.DEFAULT_CSRF_MATCHER)
                    )
                )
            }
        }
        CsrfFilter.DEFAULT_CSRF_MATCHER
        http.addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun addStuff(repo: UserRepository, penc: PasswordEncoder): CommandLineRunner = CommandLineRunner {
        val u1 = repo.findUserByUsername("someUser")
        if (u1 == null) {
            val pass = penc.encode("password")
            val user =
                User(username = "someUser", password = pass, roles = mutableListOf(Role.ADMIN), token = "MUCH_SECURE")

            repo.save(user)
        }
    }
}