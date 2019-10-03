package com.hedvig.rapio.externalservices.underwriter.transport

import com.hedvig.rapio.externalservices.underwriter.PostIncompleteQuoteResult
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "underwriterclient", url = "\${hedvig.underwriter.url:underwriter}")
interface UnderwriterClient {

    @RequestMapping(value = ["/_/v1/incompleteQuote/{id}"], method = [RequestMethod.GET])
    fun getIncompleteQuote(@PathVariable("id") id: String): ResponseEntity<GetIncompleteQuoteResult>

    @RequestMapping(value = ["/_/v1/incompleteQuote/"], method = [RequestMethod.POST])
    fun postIncompleteQuote(@RequestBody body: PostIncompleteQuoteRequest): ResponseEntity<PostIncompleteQuoteResult>

    @RequestMapping(value = ["/_/v1/incompleteQuote/{id}"], method = [RequestMethod.POST])
    fun updateIncomplete(@PathVariable("id") id: String, @RequestBody body: Any)

    @RequestMapping(value = ["/_/v1/incompleteQuote/{quoteId}/completeQuote"], method = [RequestMethod.POST])
    fun createCompleteQuote(@PathVariable("quoteId") quoteId: String) : ResponseEntity<CompleteQuoteResponse>

    @RequestMapping(value = ["/_/v1/quote/{quoteId}/sign"], method = [RequestMethod.POST])
    fun signQuote(@PathVariable("quoteId") quoteId: String) : ResponseEntity<SignQuoteResponse>

    @RequestMapping(value = ["/_/v1/quote/{quoteId}"], method = [RequestMethod.GET])
    fun getQuote(@PathVariable("quoteId") quoteId: String) : ResponseEntity<GetQuoteResponse>
}