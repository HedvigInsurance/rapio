package com.hedvig.rapio.externalservices.memberService

data class CreateMemberRequest(
    val acceptLanguage: String,
    val partner: String?
)
