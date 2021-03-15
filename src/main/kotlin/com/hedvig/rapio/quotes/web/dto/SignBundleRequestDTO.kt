package com.hedvig.rapio.quotes.web.dto

import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class SignBundleRequestDTO(
    val requestId: String,
    @get:NotEmpty val quoteIds: List<UUID>,
    @get:Valid val startsAt: Date,
    @get:Email val email: String,
    @get:NotBlank val firstName: String,
    @get:NotBlank val lastName: String,
    val personalNumber: String?,
    val monthlyPremium: Amount
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

