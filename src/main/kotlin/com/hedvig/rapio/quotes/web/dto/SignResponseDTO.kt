package com.hedvig.rapio.quotes.web.dto

data class SignResponseDTO(
        val requestId: String,
        val quoteId: String,
        val productId: String,
        val signedAt: Long
)