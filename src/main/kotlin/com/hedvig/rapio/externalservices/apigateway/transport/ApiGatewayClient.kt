package com.hedvig.rapio.externalservices.apigateway.transport

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "api-gateway", url = "\${hedvig.api-gateway.url:api-gateway}")
interface ApiGatewayClient {
    @PostMapping("/_/setupPaymentLink/create")
    fun setupPaymentLink(
        @RequestHeader token: String,
        @RequestBody dto: CreateSetupPaymentLinkRequestDto,
        @RequestParam(required = false) variation: String?
    ): ResponseEntity<CreateSetupPaymentLinkResponseDto>
}
