package com.hedvig.rapio.externalservices.memberService.dto

import com.neovisionaries.i18n.CountryCode
import java.time.LocalDate

data class UpdateMemberRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val ssn: String? = null,
    val countryCode: CountryCode? = null,
    val address: Address? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val birthDate: LocalDate? = null
) {
    data class Address(
        val street: String?,
        val city: String?,
        val zipCode: String?,
        val apartmentNo: String?,
        val floor: Int?
    )
}