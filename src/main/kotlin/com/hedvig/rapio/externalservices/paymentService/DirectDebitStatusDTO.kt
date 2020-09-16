package com.hedvig.rapio.externalservices.paymentService

data class DirectDebitStatusDTO(
    val memberId: String,
    val directDebitActivated: Boolean
)