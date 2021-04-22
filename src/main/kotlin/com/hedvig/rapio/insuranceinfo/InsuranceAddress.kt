package com.hedvig.rapio.insuranceinfo

import com.hedvig.libs.logging.masking.Masked

data class InsuranceAddress(
    @Masked val street: String,
    val postalCode: String
)
