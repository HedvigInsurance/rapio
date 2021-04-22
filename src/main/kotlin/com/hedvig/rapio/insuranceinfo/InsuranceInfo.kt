package com.hedvig.rapio.insuranceinfo

import com.hedvig.libs.logging.masking.Masked
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import java.time.LocalDate
import javax.money.MonetaryAmount

data class InsuranceInfo(
    val memberId: String?, // TODO: Remove this one
    val insuranceStatus: InsuranceStatus,
    val insurancePremium: MonetaryAmount,
    val inceptionDate: LocalDate?,
    val terminationDate: LocalDate?,
    val paymentConnected: Boolean,
    @Masked val certificateUrl: String?,
    val numberCoInsured: Int?,
    @Masked val insuranceAddress: InsuranceAddress?,
    val squareMeters: Long?
)
