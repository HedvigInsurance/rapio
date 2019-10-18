package com.hedvig.rapio.externalservices.underwriter.transport

import java.time.LocalDate
import java.time.ZoneId

data class SignQuoteRequest (
        val name: Name?,
        val date: LocalDate,
        val email: String
)

data class DateWithZone (
        val date: LocalDate,
        val timeZone: ZoneId
)

data class Name (
        val firstName: String,
        val lastName: String
)