package com.hedvig.rapio.externalservices.productPricing

import java.time.LocalDate

data class TermsAndConditions (
    val commencementDate: LocalDate,
    val displayName: String,
    val url: String
)
