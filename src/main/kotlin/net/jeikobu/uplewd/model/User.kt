package net.jeikobu.uplewd.model

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class User constructor(
    @Indexed(unique = true)
    private val username: String,
    @get:JvmName("getPassword_") var password: String,
    val roles: MutableList<Role>,
    var passwordExpired: Boolean = false,
    var expired: Boolean = false,
    var locked: Boolean = false,
): UserDetails {
    constructor() : this(username = "", password = "", roles = mutableListOf(),
        passwordExpired = false, expired = false, locked = false)

    override fun getAuthorities(): Collection<GrantedAuthority> = roles.map {
        GrantedAuthority { "ROLE_${it.roleName}" }
    }

    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = !expired
    override fun isAccountNonLocked(): Boolean = !locked
    override fun isCredentialsNonExpired(): Boolean = !passwordExpired
    override fun isEnabled(): Boolean = !locked && !expired && !passwordExpired
}