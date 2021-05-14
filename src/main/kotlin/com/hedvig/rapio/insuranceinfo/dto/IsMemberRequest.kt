package com.hedvig.rapio.insuranceinfo.dto

import com.hedvig.libs.logging.masking.Masked

data class IsMemberRequest(
    @Masked val ssn: String
)
