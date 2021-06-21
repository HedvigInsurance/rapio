package com.hedvig.rapio.externalservices.productPricing.transport

import com.hedvig.rapio.externalservices.memberService.dto.CreateTrialRequest
import com.hedvig.rapio.externalservices.memberService.dto.CreateTrialResponse
import com.hedvig.rapio.externalservices.productPricing.TermsAndConditions
import com.hedvig.rapio.externalservices.productPricing.TypeOfContract
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.util.Locale

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
    fun getTrialByMemberId(@RequestParam memberId: String): ResponseEntity<List<TrialDto>>

    @GetMapping("/_/terms/{contractType}/{locale}/{date}")
    fun getTermsAndConditions(
        @PathVariable contractType: TypeOfContract,
        @PathVariable locale: Locale,
        @PathVariable date: LocalDate,
        @RequestParam partner: String?
    ): ResponseEntity<TermsAndConditions>

    @GetMapping("/_/terms/{contractType}/{locale}")
    fun getLatestTermsAndConditions(
        @PathVariable contractType: TypeOfContract,
        @PathVariable locale: Locale,
        @RequestParam partner: String?
    ): ResponseEntity<TermsAndConditions>
}
