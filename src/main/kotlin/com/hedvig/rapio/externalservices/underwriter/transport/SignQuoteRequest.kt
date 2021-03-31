package com.hedvig.rapio.externalservices.underwriter.transport

import com.hedvig.libs.logging.masking.Masked
import java.time.LocalDate
import java.time.ZoneId

data class SignQuoteRequest (
        val name: Name?,
        @Masked val ssn: String?,
        val startDate: LocalDate,
        @Masked val email: String
)

data class DateWithZone (
        val date: LocalDate,
        val timeZone: ZoneId
)

data class Name (
        @Masked val firstName: String,
        @Masked val lastName: String
)