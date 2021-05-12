package com.hedvig.rapio.externalservices.memberService.dto

data class IsMemberRequest(
    val memberId: String?,
    val ssn: String?,
    val email: String?
)
