package com.hedvig.rapio.members.dto

import com.hedvig.libs.logging.masking.Masked
import com.hedvig.rapio.externalservices.memberService.model.TrialType
import com.neovisionaries.i18n.CountryCode
import java.time.LocalDate

data class CreateTrialMemberRequest(
    @Masked val personalNumber: String,
    @Masked val firstName: String,
    @Masked val lastName: String,
    @Masked val email: String?,
    @Masked val phoneNumber: String?,
    val countryCode: CountryCode,
    val address: Address,
    val birthDate: LocalDate,
    val fromDate: LocalDate,
    val type: TrialType
) {
    data class Address(
        @Masked val street: String,
        val city: String,
        val zipCode: String,
        @Masked val apartmentNo: String?,
        val livingSpace: Int?,
        val floor: Int?
    )
}



