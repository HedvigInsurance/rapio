package com.hedvig.rapio.externalservices.underwriter.transport

import java.time.LocalDate
import java.util.UUID

data class SignQuoteBundleRequest (
        val quoteIds: List<UUID>,
        val name: Name?,
        val ssn: String?,
        val startDate: LocalDate,
        val email: String,
        val price: String?,   // Used for bundle price verification
        val currency: String? // Used for bundle price verification
) {
        data class Name(
                val firstName: String,
                val lastName: String
        )
}

