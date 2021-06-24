package com.hedvig.rapio.externalservices.apigateway

import com.hedvig.rapio.externalservices.apigateway.transport.ApiGatewayClient
import com.hedvig.rapio.externalservices.apigateway.transport.CreateSetupPaymentLinkRequestDto
import com.hedvig.rapio.util.getCurrentlyAuthenticatedPartner
import com.neovisionaries.i18n.CountryCode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApiGateway(
    private val apiGatewayClient: ApiGatewayClient,
    @Value("\${hedvig.api-gateway.token}") private val token: String
) {

    fun setupPaymentLink(memberId: String, market: String): String? {
        return try {
            val countryCode = when (market) {
                "SWEDEN" -> CountryCode.SE
                "NORWAY" -> CountryCode.NO
                "DENMARK" -> CountryCode.DK
                else ->
                    throw RuntimeException("Unknown market: $market")
            }

            val partner = getCurrentlyAuthenticatedPartner()

            val response = apiGatewayClient.setupPaymentLink(
                token = token,
                dto = CreateSetupPaymentLinkRequestDto(memberId, countryCode),
                variation = partner.setupPaymentLinkVariation
            )
            response.body!!.url
        } catch (e: Exception) {
            logger.error("Something went wrong with setting up a payment link for member (memberId=$memberId)", e)
            null
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
