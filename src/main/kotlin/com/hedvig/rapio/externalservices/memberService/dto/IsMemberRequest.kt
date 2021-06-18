package com.hedvig.rapio.externalservices.memberService.dto

import com.hedvig.libs.logging.masking.Masked

data class IsMemberRequest(
    val memberId: String? = null,
    @Masked val ssn: String? = null,
    @Masked val email: String? = null
)
