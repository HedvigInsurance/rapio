package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import java.time.LocalDate
import javax.money.MonetaryAmount

data class InsuranceInfo(
    val memberId: String,
    val insuranceStatus: InsuranceStatus,
    val insurancePremium: MonetaryAmount,
    val inceptionDate: LocalDate?,
    val paymentConnected: Boolean?
)
