package com.hedvig.rapio.externalservices.underwriter.transport

import java.time.Instant
import java.util.UUID

data class SignedQuoteBundleResponseDto(
    val memberId: String,
    val market: String,
    val contracts: List<Contract>
) {
    data class Contract(
        val id: UUID,
        val signedAt: Instant
    )
}
