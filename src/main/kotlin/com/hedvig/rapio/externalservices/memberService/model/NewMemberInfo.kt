package com.hedvig.rapio.externalservices.memberService.model

import java.time.LocalDate

data class NewMemberInfo(
    val personalNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String?,
    val address: Address,
    val birthDate: LocalDate,
    val type: TrialType
) {
    data class Address(
        val street: String,
        val city: String,
        val zipCode: String,
        val apartmentNo: String?,
        val livingSpace: Int?,
        val floor: Int?
    )
}


