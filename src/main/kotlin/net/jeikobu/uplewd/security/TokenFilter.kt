package net.jeikobu.uplewd.security

import net.jeikobu.uplewd.component.UplewdUserDetailsService
import net.jeikobu.uplewd.db.UserRepository
import net.jeikobu.uplewd.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenFilter @Autowired constructor(
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = getTokenFromHeader(request.getHeader(HttpHeaders.AUTHORIZATION))
        if (token.isEmpty()) {
            filterChain.doFilter(request, response)
            return
        }

        val user: User? = userRepository.findUserByToken(token)
        if (user == null) {
            filterChain.doFilter(request, response)
            return
        }

        val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities).apply {
            details = WebAuthenticationDetails(request)
        }

        SecurityContextHolder.getContext().authentication = auth


        filterChain.doFilter(request, response)
    }

    private fun getTokenFromHeader(header: String?): String {
        if (header.isNullOrEmpty() || !header.startsWith("Bearer")) {
            return ""
        }

        val splitHeader = header.split(" ")
        if (splitHeader.count() < 2 || splitHeader[1].isEmpty()) {
            return ""
        }

        return splitHeader[1]
    }
}