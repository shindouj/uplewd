package net.jeikobu.uplewd.security

import org.springframework.http.HttpHeaders
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

class BearerAuthorizationMatcher : RequestMatcher {

    override fun matches(request: HttpServletRequest?): Boolean {
        val authorizationHeader = request?.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader.isNullOrEmpty()) {
            return false
        }

        val split = authorizationHeader.split(" ")
        if (split.count() < 2) {
            return false
        }

        if(split[0] != "Bearer" || split[1].isEmpty()){
            return false
        }

        return true
    }

}