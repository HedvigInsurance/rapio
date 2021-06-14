package com.hedvig.rapio.externalservices.paymentService

import com.hedvig.rapio.externalservices.paymentService.transport.DirectDebitStatusDTO
import com.hedvig.rapio.externalservices.paymentService.transport.PaymentServiceClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentService(
    val paymentServiceClient: PaymentServiceClient
) {
    fun getDirectDebitStatus(memberId: String): DirectDebitStatusDTO? {
        try {
            val response = paymentServiceClient.getDirectDebitStatusByMemberId(memberId)
            if (response.statusCode.is2xxSuccessful) {
                return response.body!!
            }
        } catch (ex: Exception) {
            logger.error("Payment service exploded while fetching direct debit status. MemberId: $memberId")
        }
        return null
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
