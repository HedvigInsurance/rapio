package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.paymentService.transport.DirectDebitStatus
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.insuranceinfo.dto.ExtendedInsuranceInfo
import com.hedvig.rapio.insuranceinfo.dto.InsuranceAddress
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import java.math.BigDecimal
import org.javamoney.moneta.Money
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
        return getInsuranceInfoFromContract(memberId) ?: getInsuranceInfoFromTrial(memberId)
    }

    fun getInsuranceInfoFromTrial(memberId: String): InsuranceInfo? {
        val trial = productPricingService.getTrialForMemberId(memberId)
        val directDebitStatus = paymentService.getDirectDebitStatus(memberId)
        return trial?.let {
            InsuranceInfo(
                memberId = memberId,
                insuranceStatus = InsuranceStatus.ACTIVE,
                insurancePremium = Money.of(BigDecimal.ZERO, "SEK"),
                inceptionDate = trial.fromDate,
                paymentConnected = directDebitStatus?.directDebitActivated ?: false
            )
        }
    }

    fun getInsuranceInfoFromContract(memberId: String): InsuranceInfo? {
        val currentContract = getCurrentContract(memberId) ?: return null
        val currentAgreement =
            currentContract.genericAgreements.find { agreement -> agreement.id == currentContract.currentAgreementId }!!
        val directDebitStatus = paymentService.getDirectDebitStatus(memberId)

        return InsuranceInfo(
            memberId = memberId, // TODO: Remove this after talking to Avy about their usage
            insuranceStatus = InsuranceStatus.fromContractStatus(currentContract.status),
            insurancePremium = currentAgreement.basePremium,
            inceptionDate = currentContract.masterInception,
            paymentConnected = directDebitStatus?.directDebitActivated ?: false
        )
    }

    fun getExtendedInsuranceInfo(memberId: String): ExtendedInsuranceInfo? {
        return getExtendedInsuranceInfoFromContract(memberId)
            ?: getExtendedInsuranceInfoFromTrial(memberId)
    }

    private fun getExtendedInsuranceInfoFromTrial(memberId: String): ExtendedInsuranceInfo? {
        val trial = productPricingService.getTrialForMemberId(memberId)
        val directDebitStatus = paymentService.getDirectDebitStatus(memberId)
        return trial?.let {
            ExtendedInsuranceInfo(
                isTrial = true,
                insuranceStatus = InsuranceStatus.ACTIVE,
                insurancePremium = Money.of(BigDecimal.ZERO, "SEK"),
                inceptionDate = trial.fromDate,
                paymentConnected = directDebitStatus?.directDebitActivated ?: false,
                terminationDate = trial.toDate,
                paymentConnectionStatus = directDebitStatus?.directDebitStatus ?: DirectDebitStatus.NEEDS_SETUP,
                certificateUrl = null,
                numberCoInsured = null,
                insuranceAddress = InsuranceAddress(
                    trial.address.street,
                    trial.address.zipCode
                ),
                squareMeters = trial.address.livingSpace?.toLong()
            )
        }
    }

    fun getExtendedInsuranceInfoFromContract(memberId: String): ExtendedInsuranceInfo? {
        val currentContract = getCurrentContract(memberId) ?: return null
        val currentAgreement =
            currentContract.genericAgreements.find { agreement -> agreement.id == currentContract.currentAgreementId }!!
        val directDebitStatus = paymentService.getDirectDebitStatus(memberId)

        return ExtendedInsuranceInfo(
            isTrial = false,
            insuranceStatus = InsuranceStatus.fromContractStatus(currentContract.status),
            insurancePremium = currentAgreement.basePremium,
            inceptionDate = currentContract.masterInception,
            terminationDate = currentContract.terminationDate,
            paymentConnected = directDebitStatus?.directDebitActivated ?: false,
            paymentConnectionStatus = directDebitStatus?.directDebitStatus ?: DirectDebitStatus.NEEDS_SETUP,
            certificateUrl = currentAgreement.certificateUrl,
            numberCoInsured = currentAgreement.numberCoInsured,
            insuranceAddress = currentAgreement.address?.let { InsuranceAddress(it.street, it.postalCode) },
            squareMeters = currentAgreement.squareMeters
        )
    }

    fun getConnectDirectDebitUrl(memberId: String): String? {
        val contractMarket = productPricingService.getContractMarketInfo(memberId)?.market ?: return null
        return apiGateway.setupPaymentLink(memberId, contractMarket)
    }
}
