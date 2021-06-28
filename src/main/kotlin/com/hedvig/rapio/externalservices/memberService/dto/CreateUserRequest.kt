package com.hedvig.rapio.externalservices.memberService.dto

import com.neovisionaries.i18n.CountryCode

data class CreateUserRequest(
    val memberId: String,
    val simpleSignConnection: SimpleSignConnectionDto
)

data class SimpleSignConnectionDto(
    val personalNumber: String,
    val countryCode: CountryCode
)
