package com.hedvig.rapio.comparison.web.dto

import java.util.*

data class QuoteResponseDTO(
        val id: UUID,
        val validUntil:Long,
        val price:Long
        )