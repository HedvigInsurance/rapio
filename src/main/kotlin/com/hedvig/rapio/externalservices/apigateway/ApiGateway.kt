package com.hedvig.rapio.externalservices.apigateway

import com.hedvig.rapio.externalservices.apigateway.transport.ApiGatewayClient
import com.hedvig.rapio.externalservices.apigateway.transport.CreateSetupPaymentLinkRequestDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApiGateway(
  private val apiGatewayClient: ApiGatewayClient,
  @Value("\${hedvig.api-gateway.token}") private val token: String
) {

  fun setupPaymentLink(memberId: String): String? {
    return try {
      val response = apiGatewayClient.setupPaymentLink(token, CreateSetupPaymentLinkRequestDto(memberId))
      response.body!!.url
    } catch (e: Exception) {
      logger.error("Something went wrong with setting up a payment link for member $memberId [ Error: $e ]")
      null
    }
  }

  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)!!
  }
}