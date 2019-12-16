package com.hedvig.rapio.quotes.web.dto

data class SignResponseDTO(
        val requestId: String,
        @Deprecated("This has the same value as productId, so use that instead", level = DeprecationLevel.ERROR)
        val quoteId: String,
        val productId: String,
        val signedAt: Long
)