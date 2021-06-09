package com.hedvig.rapio.externalservices.memberService.model

import java.time.LocalDate

data class NewMemberInfo(
    val personalNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val address: Address,
    val birthDate: LocalDate,
    val ownership: Ownership
)

data class Address(
    val street: String,
    val city: String,
    val zipCode: String,
    val apartmentNo: String?,
    val floor: Int?
)
