package com.hedvig.rapio.externalservices.memberService.dto

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.externalservices.memberService.model.TrialType
import java.time.LocalDate

data class CreateTrialRequest(
    val memberId: String,
    val fromDate: LocalDate,
    val type: TrialType,
    val partner: Partner,
    val address: Address
) {
    data class Address(
        val street: String,
        val city: String,
        val zipCode: String,
        val livingSpace: Int?,
        val apartmentNo: String?,
        val floor: Int?
    )
}
