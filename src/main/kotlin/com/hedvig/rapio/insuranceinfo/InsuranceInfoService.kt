package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import org.springframework.stereotype.Service

@Service
class InsuranceInfoService(
    val productPricingService: ProductPricingService,
    val paymentService: PaymentService
) {
    fun getInsuranceInfo(memberId: String): InsuranceInfo? {
        val contracts: List<Contract> = productPricingService.getContractsByMemberId(memberId)

        val isDirectDebitConnected = paymentService.isDirectDebitConnected(memberId)

        val contract = contracts.maxBy { contract -> contract.createdAt }!!

        return InsuranceInfo(
            memberId,
            InsuranceStatus.fromContractStatus(contract.status),
            contract.agreements.find { agreement -> agreement.id == contract.currentAgreementId }!!.basePremium,
            contract.masterInception,
            isDirectDebitConnected
        )
    }
}
