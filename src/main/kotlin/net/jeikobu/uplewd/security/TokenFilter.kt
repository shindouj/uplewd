package net.jeikobu.uplewd.security

import net.jeikobu.uplewd.component.UplewdUserDetailsService
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenFilter @Autowired constructor(
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (header.isNullOrEmpty() || !header.startsWith("Bearer")) {
            return true
        }

        val splitHeader = header.split(" ")
        if (splitHeader.count() < 2 || splitHeader[1].isEmpty()) {
            return true
        }

        return false
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1]
        val user: User? = userRepository.findUserByToken(token)
        if (user == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities).apply {
            details = WebAuthenticationDetails(request)
        }

        SecurityContextHolder.getContext().authentication = auth
        filterChain.doFilter(request, response)
    }
}