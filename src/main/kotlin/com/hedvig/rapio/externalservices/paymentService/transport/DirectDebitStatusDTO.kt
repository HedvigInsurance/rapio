package com.hedvig.rapio.externalservices.paymentService.transport

data class DirectDebitStatusDTO(
    val memberId: String,
    val directDebitActivated: Boolean,
    val directDebitStatus: DirectDebitStatus
)

enum class DirectDebitStatus {
    NEEDS_SETUP,
    PENDING,
    ACTIVATED
}
