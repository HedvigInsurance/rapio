package com.hedvig.rapio.externalservices.memberService.dto

import java.time.LocalDate

class UpdateContactInformationRequest(
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String?,
    val address: Address,
    val birthDate: LocalDate
)

data class Address(
    val street: String?,
    val city: String?,
    val zipCode: String?,
    val apartmentNo: String?,
    val floor: Int?
)
