package com.hedvig.rapio.externalservices.underwriter.transport

import java.time.Instant

data class SignedQuoteResponseDto(
    val id: String,
    val memberId: String,
    val signedAt: Instant,
    val market: String
)
