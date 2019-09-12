package com.hedvig.rapio.apikeys

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
@Profile("auth")
class ApplicationPropertiesApikeysService(@Value("#{\${hedvig.rapio.apikeys}}") val apikeys: Map<String, String>) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val apiUserName = apikeys[username]

        return User.withDefaultPasswordEncoder().username(apiUserName)
                .password("")
                .roles(Roles.COMPARISON.name).build()
    }

}