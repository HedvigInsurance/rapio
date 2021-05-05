package com.hedvig.rapio.members.dto

import java.time.LocalDate

data class CreateMemberRequest(
    val personalNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val address: Address,
    val birthDate: LocalDate
)