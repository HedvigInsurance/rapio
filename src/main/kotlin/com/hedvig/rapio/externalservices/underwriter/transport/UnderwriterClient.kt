package com.hedvig.rapio.externalservices.underwriter.transport

import com.neovisionaries.i18n.CountryCode
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "underwriterclient", url = "\${hedvig.underwriter.url:underwriter}")
interface UnderwriterClient {
    @PostMapping("/_/v1/quotes")
    fun createQuote(@RequestBody body: IncompleteQuoteDTO): ResponseEntity<CompleteQuoteResponse>

    @PostMapping("/_/v1/quotes/bundle")
    fun quoteBundle(@RequestBody body: QuoteBundleRequestDto): ResponseEntity<QuoteBundleResponseDto>

    @PostMapping("/_/v1/quotes/{quoteId}/signFromRapio")
    fun signQuote(@PathVariable("quoteId") quoteId: String, @RequestBody body: SignQuoteRequest): ResponseEntity<SignedQuoteResponseDto>

    @PostMapping("/_/v1/quotes/bundle/signFromRapio")
    fun signQuoteBundle(@RequestBody body: SignQuoteBundleRequest): ResponseEntity<SignedQuoteBundleResponseDto>

    @GetMapping("/insurance-companies")
    fun getInsuranceCompanies(countryCode:CountryCode) : ResponseEntity<List<InsuranceCompanyDto>>
}
