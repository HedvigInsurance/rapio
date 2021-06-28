package com.hedvig.rapio.externalservices.productPricing

import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.externalservices.productPricing.transport.ContractMarketInfo
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import com.hedvig.rapio.externalservices.productPricing.transport.TrialDto
import feign.FeignException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.Locale

@Service
class ProductPricingService(
    val productPricingClient: ProductPricingClient
) {

    fun getContractsByMemberId(memberId: String): List<Contract> {
        return productPricingClient.getContractsByMemberId(memberId).body!!
    }

    fun getContractMarketInfo(memberId: String): ContractMarketInfo? = try {
        productPricingClient.getContractMarketInfoByMemberId(memberId)
    } catch (exception: FeignException) {
        logger.error(exception) { "Unable to get contract market info for member (memberId=$memberId)" }
        null
    }

    fun getTrialForMemberId(memberId: String): TrialDto? = try {
        productPricingClient.getTrialByMemberId(memberId).body?.firstOrNull()
    } catch (e: FeignException) {
        logger.error(e) { "Unable to get Trial for member (memberId=$memberId)" }
        null
    }

    fun getTermsAndConditions(
        contractType: TypeOfContract,
        locale: Locale,
        date: LocalDate,
        partner: String?
    ): TermsAndConditions? = try {
        productPricingClient.getTermsAndConditions(
            contractType, locale, date, partner
        ).body
    } catch (e: FeignException) {
        logger.error(e) { "Failed to get terms for input { contractType: $contractType, locale: $locale, date: $date, partner: $partner}" }
        null
    }

    fun getLatestTermsAndConditions(
        contractType: TypeOfContract,
        locale: Locale,
        partner: String?
    ): TermsAndConditions? = try {
        productPricingClient.getLatestTermsAndConditions(
            contractType, locale, partner
        ).body
    } catch (e: FeignException) {
        logger.error(e) { "Failed to get terms for input { contractType: $contractType, locale: $locale, partner: $partner}" }
        null
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
