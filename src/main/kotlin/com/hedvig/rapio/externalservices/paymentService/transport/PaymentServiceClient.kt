package com.hedvig.rapio.externalservices.paymentService.transport

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "payment-service",
    url = "\${hedvig.payment-service.url:payment-service}"
)
interface PaymentServiceClient {
    @GetMapping("/directDebit/status")
    fun getDirectDebitStatusByMemberId(@RequestHeader("Hedvig.Token") memberId: String): ResponseEntity<DirectDebitStatusDTO?>
}
