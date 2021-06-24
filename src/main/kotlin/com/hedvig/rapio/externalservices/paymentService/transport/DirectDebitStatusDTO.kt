package com.hedvig.rapio.externalservices.paymentService.transport

import com.hedvig.rapio.insuranceinfo.dto.ExtendedInsuranceInfo.DirectDebitStatusInfo

data class DirectDebitStatusDTO(
    val memberId: String,
    val directDebitActivated: Boolean,
    val directDebitStatus: DirectDebitStatus
)

enum class DirectDebitStatus {
    NEEDS_SETUP,
    PENDING,
    ACTIVE
}

fun DirectDebitStatus.toInfo(): DirectDebitStatusInfo = when (this) {
    DirectDebitStatus.NEEDS_SETUP -> DirectDebitStatusInfo.NEEDS_SETUP
    DirectDebitStatus.PENDING -> DirectDebitStatusInfo.PENDING
    DirectDebitStatus.ACTIVE -> DirectDebitStatusInfo.ACTIVATED
}
