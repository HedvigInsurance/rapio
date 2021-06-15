package com.hedvig.rapio.externalservices.productPricing.transport

import com.hedvig.rapio.externalservices.memberService.dto.CreateTrialRequest
import com.hedvig.rapio.externalservices.memberService.dto.CreateTrialResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "productPricingClient",
    url = "\${hedvig.product-pricing.url:product-pricing}"
)
interface ProductPricingClient {
    @GetMapping("/_/contracts/members/{memberId}")
    fun getContractsByMemberId(@PathVariable("memberId") memberId: String): ResponseEntity<List<Contract>>

    @GetMapping("/_/contracts/members/{memberId}/contract/market/info")
    fun getContractMarketInfoByMemberId(@PathVariable memberId: String): ContractMarketInfo

    @PostMapping("/_/trial")
    fun createTrial(@RequestBody body: CreateTrialRequest): ResponseEntity<CreateTrialResponse>

    @GetMapping("/_/trial")
    fun getTrialByMemberId(@RequestParam memberId: String) : ResponseEntity<List<TrialDto>>
}
