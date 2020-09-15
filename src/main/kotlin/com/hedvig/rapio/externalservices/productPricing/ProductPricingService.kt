package com.hedvig.rapio.externalservices.productPricing

import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import feign.FeignException
import org.springframework.stereotype.Service

@Service
class ProductPricingService(
    val productPricingClient: ProductPricingClient
) {

    fun getContractsByMemberId(memberId: String): List<Contract>? {
        try {
            return productPricingClient.getContractsByMemberId(memberId).body
        } catch (ex: FeignException) {
            when (ex.status()) {
                404 -> return null
            }
            throw ex
        }
    }
}