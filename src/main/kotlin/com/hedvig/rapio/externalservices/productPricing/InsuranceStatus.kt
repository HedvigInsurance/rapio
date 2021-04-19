package com.hedvig.rapio.externalservices.productPricing

import com.hedvig.rapio.externalservices.productPricing.transport.ContractStatus

enum class InsuranceStatus {
    PENDING,
    ACTIVE_IN_FUTURE,
    ACTIVE,
    TERMINATED;

    companion object {
        fun fromContractStatus(contractStatus: ContractStatus): InsuranceStatus {
            return when (contractStatus) {
                ContractStatus.PENDING -> PENDING
                ContractStatus.ACTIVE_IN_FUTURE -> ACTIVE_IN_FUTURE
                ContractStatus.ACTIVE_IN_FUTURE_AND_TERMINATED_IN_FUTURE -> ACTIVE_IN_FUTURE
                ContractStatus.ACTIVE,
                ContractStatus.TERMINATED_TODAY,
                ContractStatus.TERMINATED_IN_FUTURE -> ACTIVE
                ContractStatus.TERMINATED -> TERMINATED
            }
        }
    }
}
