package com.hedvig.rapio.quotes.util

import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import org.javamoney.moneta.Money
import java.time.Instant
import java.util.UUID

object QuoteData {
    val createApartmentRequestJson = """
        {"requestId":"adads",
         "productType": "SWEDISH_APARTMENT",
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

    val createApartmentRequestJsonWithInvalidPnr = """
        {"requestId":"adads",
         "productType": "SWEDISH_APARTMENT",
         "quoteData": { 
            "personalNumber": "xxx",
            "street": "testgatan",
            "zipCode": "12345",
            "city": "Stockholm",
            "livingSpace": 42,
            "householdSize": 2,
            "productSubType": "RENT"
         }
        }
    """.trimIndent()

    val createDeprecatedApartmentRequestJson = """
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

    val createStudentRentApartmentRequestJson = """
        {"requestId":"adads",
         "productType": "HOME",
         "quoteData": { 
            "personalNumber": "191212121212",
            "street": "testgatan",
            "zipCode": "12345",
            "city": "Stockholm",
            "livingSpace": 42,
            "householdSize": 2,
            "productSubType": "STUDENT_RENT"
         }
        }
    """.trimIndent()

    val createStudentBrfApartmentRequestJson = """
        {"requestId":"adads",
         "productType": "HOME",
         "quoteData": { 
            "personalNumber": "191212121212",
            "street": "testgatan",
            "zipCode": "12345",
            "city": "Stockholm",
            "livingSpace": 42,
            "householdSize": 2,
            "productSubType": "STUDENT_BRF"
         }
        }
    """.trimIndent()

    val createHouseRequestJson = """
        {
            "requestId": "1231a",
            "productType": "SWEDISH_HOUSE",
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

    val createDeprecatedHouseRequestJson = """
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

    val createNorwegianTravelRequestJson = """
        {
            "requestId": "1231a",
            "productType": "NORWEGIAN_TRAVEL",
            "quoteData": {
                "birthDate": "1999-01-01",
                "coInsured": 2,
                "youth": false
            }
        }
    """.trimIndent()

    val quoteResponse = QuoteResponseDTO(
        requestId = "adads",
        monthlyPremium = Money.of(123, "SEK"),
        quoteId = UUID.randomUUID().toString(),
        validUntil = Instant.now().epochSecond
    )

    val signRequestJson = """
        {
            "requestId": "jl",
            "startsAt": {
                "date": "2019-11-01",
                "timezone": "Europe/Stockholm"
            },
            "email": "some@test.com",
            "firstName": "test",
            "lastName": "Tolvansson"
        }
    """.trimIndent()

    fun makeSignResponse(id: UUID = UUID.randomUUID()) = SignResponseDTO(
        requestId = "jl",
        quoteId = id.toString(),
        productId = id.toString(),
        signedAt = Instant.now().epochSecond,
        completionUrl = "PAYMENT_REDIRECTION"
    )
}
