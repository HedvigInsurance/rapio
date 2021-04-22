package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.insuranceinfo.dto.ExtendedInsuranceInfo
import com.hedvig.rapio.insuranceinfo.dto.InsuranceAddress
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import org.springframework.stereotype.Service

@Service
class InsuranceInfoService(
    val productPricingService: ProductPricingService,
    val paymentService: PaymentService,
    val apiGateway: ApiGateway
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
        val currentAgreement =
            currentContract.genericAgreements.find { agreement -> agreement.id == currentContract.currentAgreementId }!!
        val isDirectDebitConnected = paymentService.isDirectDebitConnected(memberId)

        return InsuranceInfo(
            memberId = memberId, // TODO: Remove this after talking to Avy about their usage
            insuranceStatus = InsuranceStatus.fromContractStatus(currentContract.status),
            insurancePremium = currentAgreement.basePremium,
            inceptionDate = currentContract.masterInception,
            paymentConnected = isDirectDebitConnected
        )
    }

    fun getExtendedInsuranceInfo(memberId: String): ExtendedInsuranceInfo? {
        val currentContract = getCurrentContract(memberId) ?: return null
        val currentAgreement =
            currentContract.genericAgreements.find { agreement -> agreement.id == currentContract.currentAgreementId }!!
        val isDirectDebitConnected = paymentService.isDirectDebitConnected(memberId)

        return ExtendedInsuranceInfo(
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

    fun getConnectDirectDebitUrl(memberId: String): String? {
        val contractMarket = productPricingService.getContractMarket(memberId)?.market ?: return null
        return apiGateway.setupPaymentLink(memberId, contractMarket)
    }
}
