package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import java.time.LocalDate
import javax.money.MonetaryAmount

data class ExtendedInsuranceInfo(
    val insuranceStatus: InsuranceStatus,
    val insurancePremium: MonetaryAmount,
    val inceptionDate: LocalDate?,
    val terminationDate: LocalDate?,
    val paymentConnected: Boolean,
    val certificateUrl: String?,
    val numberCoInsured: Int?,
    val insuranceAddress: InsuranceAddress?,
    val squareMeters: Long?
)