package com.hedvig.rapio.externalservices.memberService.dto

data class IsMemberRequest(
    val memberId: String?,
    @Masked val ssn: String?,
    @Masked val email: String?
)
