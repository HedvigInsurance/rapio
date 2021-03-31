package com.hedvig.rapio.externalservices.underwriter.transport

import com.hedvig.libs.logging.masking.Masked
import java.time.LocalDate
import java.util.UUID

data class SignQuoteBundleRequest (
        val quoteIds: List<UUID>,
        val name: Name?,
        @Masked val ssn: String?,
        val startDate: LocalDate,
        @Masked val email: String,
        val price: String?,   // Used for bundle price verification
        val currency: String? // Used for bundle price verification
) {
        data class Name(
                @Masked val firstName: String,
                @Masked val lastName: String
        )
}

