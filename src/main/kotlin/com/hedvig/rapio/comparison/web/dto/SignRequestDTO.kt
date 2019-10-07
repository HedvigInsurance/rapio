package com.hedvig.rapio.comparison.web.dto

import java.time.LocalDate
import java.time.ZoneId

data class SignRequestDTO(
        val requestId:String,
        val startsAt: Date,
        val email:String,
        val firstName: String,
        val lastName: String
        )

data class Date(
        val date: LocalDate,
        val timezone: ZoneId
)