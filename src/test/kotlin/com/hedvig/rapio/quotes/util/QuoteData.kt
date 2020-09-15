package com.hedvig.rapio.quotes.util

import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import org.javamoney.moneta.Money
import java.time.Instant
import java.util.UUID

object QuoteData {
    val createApartmentRequestJson = """
        {"requestId":"adads",
         "productType": "HOME",
         "quoteData": { 
            "personalNumber": "191212121212",
            "street": "testgatan",
            "zipCode": "12345",
            "city": "Stockholm",
            "livingSpace": 42,
            "householdSize": 2,
            "productSubType": "RENT"
         }
        }
    """.trimIndent()

    val createHouseRequestJson = """
        {
            "requestId": "1231a",
            "productType": "HOUSE",
            "quoteData": {
                "street": "harry",
                "zipCode": "11216",
                "city": "stockholm",
                "livingSpace": "240",
                "personalNumber": "191212121212",
                "householdSize": "4",
                "ancilliaryArea": "123",
                "yearOfConstruction": "1976",
                "numberOfBathrooms": "2",
                "extraBuildings": [
                ],
                "isSubleted": "false",
                "floor": "2"
            }
        }
    """.trimIndent()

    val quoteResponse = QuoteResponseDTO(
    requestId = "adads",
    monthlyPremium = Money.of(123, "SEK"),
    quoteId = UUID.randomUUID().toString(),
    validUntil = Instant.now().epochSecond
    )
}