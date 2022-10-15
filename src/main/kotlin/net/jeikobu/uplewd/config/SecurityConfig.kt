package net.jeikobu.uplewd.config

import net.jeikobu.uplewd.component.UplewdUserDetailsService
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.Role
import net.jeikobu.uplewd.model.User
import net.jeikobu.uplewd.security.TokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.util.matcher.AndRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver

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
    fun getTokenFilter(userRepository: UserRepository): TokenFilter {
        return TokenFilter(userRepository)
    }

    @Bean
    fun filterChain(http: HttpSecurity, tokenFilter: TokenFilter): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize("/delete/**", permitAll)
                authorize("/admin/**", hasRole(Role.ADMIN.roleName))
                authorize(anyRequest, authenticated)
            }
            formLogin { }
            httpBasic { }
            csrf {
                requireCsrfProtectionMatcher = OrRequestMatcher(
                    CsrfFilter.DEFAULT_CSRF_MATCHER,
                    NegatedRequestMatcher(
                        RequestHeaderRequestMatcher(HttpHeaders.AUTHORIZATION)
                    )
                )
            } //TODO: remove session id cookie on rest api calls
        }

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
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