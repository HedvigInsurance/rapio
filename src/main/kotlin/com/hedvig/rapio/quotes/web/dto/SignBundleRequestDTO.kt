package com.hedvig.rapio.quotes.web.dto

import com.hedvig.libs.logging.masking.Masked
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class SignBundleRequestDTO(
    val requestId: String,
    @get:NotEmpty val quoteIds: List<UUID>,
    @get:Valid val startsAt: Date,
    @get:Email @Masked val email: String,
    @get:NotBlank @Masked val firstName: String,
    @get:NotBlank @Masked val lastName: String,
    @Masked val personalNumber: String?,
    val monthlyPremium: Amount,
    val externalMemberId: UUID?
) {
    data class Date(
        @FutureOrPresent val date: LocalDate,
        val timezone: ZoneId
    )

    data class Amount(
        val amount: String,
        val currency: String
    )
}
