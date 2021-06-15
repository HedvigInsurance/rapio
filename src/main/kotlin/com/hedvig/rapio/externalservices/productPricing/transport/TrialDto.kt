package com.hedvig.rapio.externalservices.productPricing.transport

import com.hedvig.rapio.externalservices.memberService.model.TrialType
import java.time.LocalDate
import java.util.UUID

data class TrialDto(
    val id: UUID,
    val memberId: String,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val type: TrialType,
    val partner: String,
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