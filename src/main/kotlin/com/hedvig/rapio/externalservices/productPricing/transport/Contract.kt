package com.hedvig.rapio.externalservices.productPricing.transport

import java.time.LocalDate
import java.util.*
import javax.money.CurrencyUnit

data class Contract(
    val id: UUID,
    val holderMemberId: String,
    val masterInception: LocalDate?,
    val status: String,
    val terminationDate: LocalDate?,
    val currentAgreementId: UUID,
    val agreements: List<Agreement>,
    val preferredCurrency: CurrencyUnit,
    val market: String
)
