package com.hedvig.rapio.comparison.domain

import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.*
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.persistence.Entity

@TypeDefs(
        TypeDef(name = "json", typeClass = JsonStringType::class),
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class))
@Entity
class ComparisonQuoteRequest  {
    @field:Id
    @field:GeneratedValue(generator = "UUID")
    @field:GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID? = null

    @field:CreationTimestamp
    var requestTime : Instant? = null

    @field:Type(type="jsonb")
    @field:Column(columnDefinition = "jsonb")
    var quoteData : QuoteRequest2? = null
}

data class QuoteRequest2(
        val id: Int = -1,
        val street: String,
        val zipCode: String,
        val city: String,
        val livingSpace: Int,
        val personalNumber: String,
        val phoneNumber: String,
        val householdSize: Int
) {
    @CreationTimestamp
    lateinit var requestedAt: Instant

    companion object {
        fun from(dto: QuoteRequestDTO): QuoteRequest2 = QuoteRequest2(
                street = dto.quoteData.street,
                zipCode = dto.quoteData.zipCode,
                city = dto.quoteData.city,
                livingSpace = dto.quoteData.livingSpace,
                householdSize = dto.quoteData.householdSize,
                personalNumber = dto.quoteData.personalNumber,
                phoneNumber = ""
        )
    }

}