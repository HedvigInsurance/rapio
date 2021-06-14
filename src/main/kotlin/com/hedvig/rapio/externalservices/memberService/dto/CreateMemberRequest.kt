package com.hedvig.rapio.externalservices.memberService.dto

data class CreateMemberRequest(
    val acceptLanguage: String,
    val partner: String?
)
