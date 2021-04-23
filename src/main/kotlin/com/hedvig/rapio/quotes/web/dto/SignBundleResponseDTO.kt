package com.hedvig.rapio.quotes.web.dto

import java.util.UUID

data class SignBundleResponseDTO(
    val requestId: String,
    val productIds: List<String>,
    val externalMemberId: UUID?,
    val signedAt: Long,
    val completionUrl: String?
)
