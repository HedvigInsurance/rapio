package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import org.springframework.stereotype.Service

@Service
class InsuranceInfoService(
    val productPricingService: ProductPricingService
) {

    fun getInsuranceInfo(memberId: String): InsuranceInfo? {
        val contracts: List<Contract> = productPricingService.getContractsByMemberId(memberId) ?: return null

        if (contracts.size != 1) {
            throw IllegalStateException("Should have only one contract")
        }

        val contract = contracts[0]

        return InsuranceInfo(
            memberId,
            contract.status,
            contract.agreements.find { x -> x.id == contract.currentAgreementId }!!.basePremium,
            contract.masterInception,
            null
        )
    }

}