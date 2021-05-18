package com.hedvig.rapio.quotes.web.dto

import com.hedvig.libs.logging.masking.Masked
import java.time.LocalDate
import java.time.ZoneId
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank

data class SignRequestDTO(
    val requestId: String,
    @get:Valid val startsAt: Date?,
    val currentInsuranceCompanyId: String?,

    @get:Email @Masked val email: String,

    @get:NotBlank @Masked val firstName: String,
    @get:NotBlank @Masked val lastName: String,

    @Masked val personalNumber: String?
) {
    data class Date(
        @FutureOrPresent val date: LocalDate,
        val timezone: ZoneId
    )
}
