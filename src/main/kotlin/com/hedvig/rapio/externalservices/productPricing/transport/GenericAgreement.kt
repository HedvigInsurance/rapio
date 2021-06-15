package com.hedvig.rapio.externalservices.productPricing.transport

import java.time.LocalDate
import java.util.UUID
import javax.money.MonetaryAmount

data class GenericAgreement(
    val id: UUID,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
    val basePremium: MonetaryAmount,
    val certificateUrl: String?,
    val address: Address?,
    val numberCoInsured: Int?,
    val squareMeters: Long?
) {
    data class Address(
        val street: String,
        val postalCode: String
    )
}
