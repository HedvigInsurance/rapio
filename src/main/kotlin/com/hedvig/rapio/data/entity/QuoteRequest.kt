package com.hedvig.rapio.data.entity

import com.hedvig.rapio.web.dto.QuoteRequestDTO
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class QuoteRequest(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quote_request_id_sequence")
  val id: Int = -1,
  val street: String,
  val zipCode: String,
  val city: String,
  val livingSpace: Int,
  val insuranceCoverageOverAMillion: Boolean,
  val homeOwner: Boolean,
  val deductible: Int,
  val personalNumber: String,
  val phoneNumber: String,
  val allRisk: Boolean,
  val leisureCoverage: Boolean,
  val increasedTravelCoverage: Boolean,
  val golfInsurance: Boolean,
  val securityDoor: Boolean,
  val alarmConnectedToAlarmCenter: Boolean,
  val paymentInterval: String,
  val householdSize: Int
) {
  @CreationTimestamp
  lateinit var requestedAt: Instant

  companion object {
    fun from(dto: QuoteRequestDTO): QuoteRequest = QuoteRequest(
      street = dto.street,
      zipCode = dto.zipCode,
      city = dto.city,
      livingSpace = dto.livingSpace,
      insuranceCoverageOverAMillion = dto.insuranceCoverageOverAMillion,
      homeOwner = dto.homeOwner,
      deductible = dto.deductible,
      personalNumber = dto.personalNumber,
      phoneNumber = dto.phoneNumber,
      allRisk = dto.allRisk,
      leisureCoverage = dto.leisureCoverage,
      increasedTravelCoverage = dto.increasedTravelCoverage,
      golfInsurance = dto.golfInsurance,
      securityDoor = dto.securityDoor,
      alarmConnectedToAlarmCenter = dto.alarmConnectedToAlarmCenter,
      paymentInterval = dto.paymentInterval,
      householdSize = dto.householdSize
    )
  }

}