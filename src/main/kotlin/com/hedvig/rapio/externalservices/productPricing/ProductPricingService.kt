package com.hedvig.rapio.externalservices.productPricing

import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import org.springframework.stereotype.Service

@Service
class ProductPricingService(
    val productPricingClient: ProductPricingClient
) {

    fun getContractsByMemberId(memberId: String): List<Contract> {
        return productPricingClient.getContractsByMemberId(memberId).body!!
    }
}
