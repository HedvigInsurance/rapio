package com.hedvig.rapio.insuranceinfo

import java.time.LocalDate
import javax.money.MonetaryAmount

data class InsuranceInfo(
    val memberId: String,
    val insuranceStatus: String,
    val insurancePremium: MonetaryAmount,
    val inceptionDate: LocalDate?,
    val paymentConnected: Boolean?
)
