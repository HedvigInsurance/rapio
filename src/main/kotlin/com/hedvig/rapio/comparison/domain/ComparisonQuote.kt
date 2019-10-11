package com.hedvig.rapio.comparison.domain

import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO
import org.jdbi.v3.json.Json
import org.postgresql.util.PGmoney
import org.springframework.format.number.money.MonetaryAmountFormatter
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.money.MonetaryAmount


data class ComparisonQuote(
        val id: UUID,
        val requestTime: Instant = Instant.now(),
        val requestId : String = "",
        @get:Json
        @param:Json
        val quoteData: QuoteData,
        val underwriterQuoteId: String? = null,
        val signed:Boolean = false,
        val validTo:Instant? = null) {

    /*
    fun getValidTo():Instant {
        return requestTime.atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
    }*/
}


data class CompleteQuote (val id:String, val price: MonetaryAmount)

data class QuoteData(
        val street: String,
        val zipCode: String,
        val city: String,
        val livingSpace: Int,
        val personalNumber: String,
        val phoneNumber: String,
        val householdSize: Int,
        val brf: Boolean = false
) {

    companion object {
        fun from(dto: QuoteRequestDTO): QuoteData = QuoteData(
                street = dto.quoteData.street,
                zipCode = dto.quoteData.zipCode,
                city = dto.quoteData.city,
                livingSpace = dto.quoteData.livingSpace,
                householdSize = dto.quoteData.householdSize,
                personalNumber = dto.quoteData.personalNumber,
                phoneNumber = "",
                brf = false
        )
    }
}

data class ComparisonQuoteResult(
        val id: UUID,
        val price : MonetaryAmountFormatter
)