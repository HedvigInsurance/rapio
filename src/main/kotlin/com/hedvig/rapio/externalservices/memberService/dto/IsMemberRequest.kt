package com.hedvig.rapio.externalservices.memberService.dto

import com.hedvig.libs.logging.masking.Masked

data class IsMemberRequest(
    val memberId: String?,
    @Masked val ssn: String?,
    @Masked val email: String?
)
