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
    @Masked val birthDate: LocalDate,
    val fromDate: LocalDate,
    val type: TrialType
) {
    data class Address(
        @Masked val street: String,
        @Masked val city: String,
        @Masked val zipCode: String,
        @Masked val apartmentNo: String?,
        @Masked val livingSpace: Int?,
        @Masked val floor: Int?
    )
}




