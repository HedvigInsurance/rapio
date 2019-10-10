package com.hedvig.rapio.comparison.web.dto

import java.time.Instant

data class SignResponseDTO(
        val id: String,
        val signedAt: Instant
)