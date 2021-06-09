package com.hedvig.rapio.members.dto

import com.hedvig.rapio.externalservices.memberService.model.Ownership
import java.time.LocalDate

data class CreateTrialMemberRequest(
    val personalNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String?,
    val address: Address,
    val birthDate: LocalDate,
    val fromDate: LocalDate,
    val ownership: Ownership
)

data class Address(
    val street: String,
    val city: String,
    val zipCode: String,
    val apartmentNo: String?,
    val floor: Int?
)


