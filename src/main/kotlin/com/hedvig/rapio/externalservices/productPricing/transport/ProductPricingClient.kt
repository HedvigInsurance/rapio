package com.hedvig.rapio.externalservices.productPricing.transport

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "productPricingClient",
    url = "\${hedvig.product-pricing.url:product-pricing}"
)
interface ProductPricingClient {
    @GetMapping("/_/contracts/members/{memberId}")
    fun getContractsByMemberId(@PathVariable("memberId") memberId: String): ResponseEntity<List<Contract>>

    @GetMapping("/_/contracts/members/{memberId}/contract/market/info")
    fun getContractMarketInfoByMemberId(@PathVariable memberId: String): ContractMarketInfo
}
