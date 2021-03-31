package com.hedvig.rapio.externalservices.underwriter.transport

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "underwriterclient", url = "\${hedvig.underwriter.url:underwriter}")
interface UnderwriterClient {

    @RequestMapping(value = ["/_/v1/quotes"], method = [RequestMethod.POST])
    fun createQuote(@RequestBody body: IncompleteQuoteDTO): ResponseEntity<CompleteQuoteResponse>

    @RequestMapping(value = ["/_/v1/quotes/bundle"], method = [RequestMethod.POST])
    fun quoteBundle(@RequestBody body: QuoteBundleRequestDto) : ResponseEntity<QuoteBundleResponseDto>

    @RequestMapping(value = ["/_/v1/quotes/{quoteId}/signFromRapio"], method = [RequestMethod.POST])
    fun signQuote(@PathVariable("quoteId") quoteId: String, @RequestBody body:SignQuoteRequest) : ResponseEntity<SignedQuoteResponseDto>

    @RequestMapping(value = ["/_/v1/quotes/bundle/sign"], method = [RequestMethod.POST])
    fun signQuoteBundle(@RequestBody body:SignQuoteBundleRequest) : ResponseEntity<SignedQuoteBundleResponseDto>
}