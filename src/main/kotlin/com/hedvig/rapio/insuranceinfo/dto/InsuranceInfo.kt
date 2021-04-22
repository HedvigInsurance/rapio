package com.hedvig.rapio.insuranceinfo.dto

import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import java.time.LocalDate
import javax.money.MonetaryAmount

data class InsuranceInfo(
    val memberId: String?, // TODO: Remove this one
    val insuranceStatus: InsuranceStatus,
    val insurancePremium: MonetaryAmount,
    val inceptionDate: LocalDate?,
    val paymentConnected: Boolean
)
