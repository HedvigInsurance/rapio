package com.hedvig.rapio.externalservices.memberService.dto

import com.neovisionaries.i18n.CountryCode

data class CreateMemberRequest(
    val acceptLanguage: CountryCode,
    val partner: String?
)
