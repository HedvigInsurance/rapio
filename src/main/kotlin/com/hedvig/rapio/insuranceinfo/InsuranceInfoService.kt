package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.memberService.model.toContractType
import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.paymentService.transport.DirectDebitStatus
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.productPricing.TermsAndConditions
import com.hedvig.rapio.externalservices.productPricing.TypeOfContract
import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.insuranceinfo.dto.ExtendedInsuranceInfo
import com.hedvig.rapio.insuranceinfo.dto.InsuranceAddress
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Locale
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
        val trial = productPricingService.getTrialForMemberId(memberId) ?: return null
        val directDebitStatus = paymentService.getDirectDebitStatus(memberId)

        val termsAndConditions = getTermsAndConditions(trial.type.toContractType(), null, trial.fromDate, trial.partner)

        return ExtendedInsuranceInfo(
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
            squareMeters = trial.address.livingSpace?.toLong(),
            termsAndConditions = termsAndConditions?.url ?: ""
        )
    }

    fun getExtendedInsuranceInfoFromContract(memberId: String): ExtendedInsuranceInfo? {
        val contract = getCurrentContract(memberId) ?: return null
        val agreement =
            contract.genericAgreements.find { agreement -> agreement.id == contract.currentAgreementId }!!
        val directDebitStatus = paymentService.getDirectDebitStatus(memberId)

        val termsAndConditions =
            getTermsAndConditions(contract.typeOfContract, null, agreement.fromDate, agreement.partner)

        return ExtendedInsuranceInfo(
            isTrial = false,
            insuranceStatus = InsuranceStatus.fromContractStatus(contract.status),
            insurancePremium = agreement.basePremium,
            inceptionDate = contract.masterInception,
            terminationDate = contract.terminationDate,
            paymentConnected = directDebitStatus?.directDebitActivated ?: false,
            paymentConnectionStatus = directDebitStatus?.directDebitStatus ?: DirectDebitStatus.NEEDS_SETUP,
            certificateUrl = agreement.certificateUrl,
            numberCoInsured = agreement.numberCoInsured,
            insuranceAddress = agreement.address?.let { InsuranceAddress(it.street, it.postalCode) },
            squareMeters = agreement.squareMeters,
            termsAndConditions = termsAndConditions?.url ?: ""
        )
    }

    private fun getTermsAndConditions(
        type: TypeOfContract,
        language: String?,
        date: LocalDate?,
        partner: String?
    ): TermsAndConditions? {
        val countryCode = type.name.split("_").first()
        val locale = Locale(language ?: "en", countryCode)
        return if (date != null) {
            productPricingService.getTermsAndConditions(type, locale, date, partner)
        } else {
            productPricingService.getLatestTermsAndConditions(type, locale, partner)
        }
    }

    fun getConnectDirectDebitUrl(memberId: String): String? {
        val contractMarket = productPricingService.getContractMarketInfo(memberId)?.market ?: return null
        return apiGateway.setupPaymentLink(memberId, contractMarket)
    }
}
