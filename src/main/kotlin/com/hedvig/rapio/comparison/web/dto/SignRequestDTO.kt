package com.hedvig.rapio.comparison.web.dto

import java.time.LocalDate
import java.time.ZoneId
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank

data class SignRequestDTO(
        val requestId:String,
        @get:Valid val startsAt: Date,

        @get:Email val email:String,

        @get:NotBlank val firstName: String,
        @get:NotBlank val lastName: String
        )

data class Date(
        @FutureOrPresent val date: LocalDate,
        val timezone: ZoneId
)