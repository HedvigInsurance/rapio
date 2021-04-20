package com.hedvig.rapio.util

import com.hedvig.rapio.apikeys.Partner
import org.springframework.security.core.context.SecurityContextHolder

fun getCurrentlyAuthenticatedPartner(): Partner {
    val currentUserName = SecurityContextHolder.getContext().authentication.name
    return Partner.valueOf(currentUserName)
}
