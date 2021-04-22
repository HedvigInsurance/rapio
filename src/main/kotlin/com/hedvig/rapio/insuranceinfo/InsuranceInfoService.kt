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
    private fun getCurrentContract(memberId: String): Contract? {
        val contracts: List<Contract> = productPricingService.getContractsByMemberId(memberId)

        if (contracts.isEmpty()) {
            return null
        }

        return contracts.maxBy { contract -> contract.createdAt }!!
    }

    fun getInsuranceInfo(memberId: String): InsuranceInfo? {
        val currentContract = getCurrentContract(memberId) ?: return null
        val currentAgreement = currentContract.genericAgreements.find { agreement -> agreement.id == currentContract.currentAgreementId }!!
        val isDirectDebitConnected = paymentService.isDirectDebitConnected(memberId)

        return InsuranceInfo(
            memberId = memberId, // TODO: Remove this after talking to Avy about their usage
            insuranceStatus = InsuranceStatus.fromContractStatus(currentContract.status),
            insurancePremium = currentAgreement.basePremium,
            inceptionDate = currentContract.masterInception,
            terminationDate = null,
            paymentConnected = isDirectDebitConnected,
            certificateUrl = null,
            numberCoInsured = null,
            insuranceAddress = null,
            squareMeters = null
        )
    }

    fun getExtendedInsuranceInfo(memberId: String): InsuranceInfo? {
        val currentContract = getCurrentContract(memberId) ?: return null
        val currentAgreement = currentContract.genericAgreements.find { agreement -> agreement.id == currentContract.currentAgreementId }!!
        val isDirectDebitConnected = paymentService.isDirectDebitConnected(memberId)

        return InsuranceInfo(
            memberId = null,
            insuranceStatus = InsuranceStatus.fromContractStatus(currentContract.status),
            insurancePremium = currentAgreement.basePremium,
            inceptionDate = currentContract.masterInception,
            terminationDate = currentContract.terminationDate,
            paymentConnected = isDirectDebitConnected,
            certificateUrl = currentAgreement.certificateUrl,
            numberCoInsured = currentAgreement.numberCoInsured,
            insuranceAddress = currentAgreement.address?.let { InsuranceAddress(it.street, it.postalCode) },
            squareMeters = currentAgreement.squareMeters
        )
    }
}
