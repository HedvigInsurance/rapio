package com.hedvig.rapio.externalservices.apigateway.transport

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "api-gateway", url = "\${hedvig.api-gateway.url:api-gateway}")
interface ApiGatewayClient {
  @RequestMapping(value = ["/_/setupPaymentLink/create"], method = [RequestMethod.POST])
  fun setupPaymentLink(
    @RequestHeader token: String,
    @RequestBody dto: CreateSetupPaymentLinkRequestDto
  ): ResponseEntity<CreateSetupPaymentLinkResponseDto>
}