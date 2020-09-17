package com.hedvig.rapio.externalservices.productPricing.transport

import java.util.UUID
import javax.money.MonetaryAmount

data class Agreement(
    val id: UUID,
    val basePremium: MonetaryAmount
)