package com.hedvig.rapio.comparison.web.dto

data class SignResponseDTO(
        val requestId: String,
        val quoteId: String,
        val signedAt: Long
)