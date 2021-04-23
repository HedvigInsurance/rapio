package com.hedvig.rapio.externalservices.productPricing

import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.externalservices.productPricing.transport.ContractMarketInfo
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import com.hedvig.rapio.insuranceinfo.InsuranceInfoService
import feign.FeignException
import mu.KotlinLogging
import org.springframework.stereotype.Service

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
        logger.error (exception) { "Unable to get contract market info for member (memberId=$memberId)" }
        null
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
