package com.hedvig.rapio.externalservices.memberService.dto

import java.time.LocalDate

class UpdateContactInformationRequest(
    val memberId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val address: Address? = null,
    val birthDate: LocalDate? = null
)

data class Address(
    val street: String?,
    val city: String?,
    val zipCode: String?,
    val apartmentNo: String?,
    val floor: Int?
)
