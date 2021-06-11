package com.hedvig.rapio.insuranceinfo.dto

import com.hedvig.libs.logging.masking.Masked
import com.hedvig.rapio.externalservices.paymentService.transport.DirectDebitStatus
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import java.time.LocalDate
import javax.money.MonetaryAmount

data class ExtendedInsuranceInfo(
    val insuranceStatus: InsuranceStatus,
    val insurancePremium: MonetaryAmount,
    val inceptionDate: LocalDate?,
    val terminationDate: LocalDate?,
    val paymentConnected: Boolean,
    val paymentConnectionStatus: DirectDebitStatus,
    @Masked val certificateUrl: String?,
    val numberCoInsured: Int?,
    @Masked val insuranceAddress: InsuranceAddress?,
    val squareMeters: Long?
)
