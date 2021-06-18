package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.memberService.MemberService
import com.hedvig.rapio.externalservices.memberService.model.TrialType
import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.paymentService.transport.DirectDebitStatus
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.productPricing.TypeOfContract
import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.insuranceinfo.dto.ExtendedInsuranceInfo
import com.hedvig.rapio.insuranceinfo.dto.InsuranceAddress
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import com.hedvig.rapio.util.let2
import com.hedvig.rapio.util.let3
import java.math.BigDecimal
import java.util.Locale
import org.javamoney.moneta.Money
import org.springframework.stereotype.Service

@Service
class InsuranceInfoService(
    val productPricingService: ProductPricingService,
    val paymentService: PaymentService,
    val apiGateway: ApiGateway,
    val memberService: MemberService
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
        val member = memberService.getMember(memberId)
        val trial = productPricingService.getTrialForMemberId(memberId)
        val directDebitStatus = paymentService.getDirectDebitStatus(memberId)
        return (member to trial).let2 { m, t ->
            val termsAndConditions =
                Triple(getAvyContractType(t.type), m.acceptLanguage, m.country)
                    .let3 { type, lang, country ->
                        productPricingService.getTermsAndConditions(type, Locale(lang, country), t.fromDate)
                    }

            ExtendedInsuranceInfo(
                isTrial = true,
                insuranceStatus = InsuranceStatus.ACTIVE,
                insurancePremium = Money.of(BigDecimal.ZERO, "SEK"),
                inceptionDate = t.fromDate,
                paymentConnected = directDebitStatus?.directDebitActivated ?: false,
                terminationDate = t.toDate,
                paymentConnectionStatus = directDebitStatus?.directDebitStatus ?: DirectDebitStatus.NEEDS_SETUP,
                certificateUrl = null,
                numberCoInsured = null,
                insuranceAddress = InsuranceAddress(
                    t.address.street,
                    t.address.zipCode
                ),
                squareMeters = t.address.livingSpace?.toLong(),
                termsAndConditions = termsAndConditions?.url ?: ""
            )
        }
    }

    fun getAvyContractType(type: TrialType): TypeOfContract? = try {
        if (type == TrialType.SE_APARTMENT_RENT)
            TypeOfContract.valueOf("${type}_PARTNER_AVY")
        else
            TypeOfContract.valueOf(type.name)
    } catch (e: IllegalArgumentException) {
        null
    }

    fun getExtendedInsuranceInfoFromContract(memberId: String): ExtendedInsuranceInfo? {
        val member = memberService.getMember(memberId)
        val contract = getCurrentContract(memberId)
        return (member to contract).let2 { m, c ->
            val currentAgreement =
                c.genericAgreements.find { agreement -> agreement.id == c.currentAgreementId }!!
            val directDebitStatus = paymentService.getDirectDebitStatus(memberId)

            val termsAndConditions =
                Triple(m.acceptLanguage, m.country, currentAgreement.fromDate)
                    .let3 { lang, country, date ->
                        productPricingService.getTermsAndConditions(c.typeOfContract, Locale(lang, country), date)
                    }

            ExtendedInsuranceInfo(
                isTrial = false,
                insuranceStatus = InsuranceStatus.fromContractStatus(c.status),
                insurancePremium = currentAgreement.basePremium,
                inceptionDate = c.masterInception,
                terminationDate = c.terminationDate,
                paymentConnected = directDebitStatus?.directDebitActivated ?: false,
                paymentConnectionStatus = directDebitStatus?.directDebitStatus ?: DirectDebitStatus.NEEDS_SETUP,
                certificateUrl = currentAgreement.certificateUrl,
                numberCoInsured = currentAgreement.numberCoInsured,
                insuranceAddress = currentAgreement.address?.let { InsuranceAddress(it.street, it.postalCode) },
                squareMeters = currentAgreement.squareMeters,
                termsAndConditions = termsAndConditions?.url ?: ""
            )
        }
    }

    fun getConnectDirectDebitUrl(memberId: String): String? {
        val contractMarket = productPricingService.getContractMarketInfo(memberId)?.market ?: return null
        return apiGateway.setupPaymentLink(memberId, contractMarket)
    }
}
