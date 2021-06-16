package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberRepository
import com.hedvig.rapio.externalservices.memberService.model.TrialType
import com.hedvig.rapio.externalservices.paymentService.transport.DirectDebitStatus
import com.hedvig.rapio.externalservices.paymentService.transport.DirectDebitStatusDTO
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.externalservices.productPricing.transport.Contract
import com.hedvig.rapio.externalservices.productPricing.transport.ContractStatus
import com.hedvig.rapio.externalservices.productPricing.transport.GenericAgreement
import com.hedvig.rapio.externalservices.productPricing.transport.TrialDto
import com.hedvig.rapio.helpers.IntegrationTest
import io.mockk.every
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class InsuranceInfoControllerTest : IntegrationTest() {

    @Autowired
    lateinit var externalMemberRepository: ExternalMemberRepository

    @AfterEach
    fun teardown() {
        reset {
            entity<ExternalMember>()
        }
    }

    @Test
    fun `retrieving member info returns not found if no insurance info`() {
        val memberId = "123456"
        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        every { productPricingClient.getTrialByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        client.get("/v1/members/$memberId").assertStatus(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `can use regular insurance endpoint with member id`() {
        val memberId = "123456"
        val agreementId = UUID.randomUUID()
        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(
            listOf(
                Contract(
                    id = UUID.randomUUID(),
                    holderMemberId = memberId,
                    masterInception = LocalDate.now(),
                    status = ContractStatus.ACTIVE,
                    terminationDate = null,
                    currentAgreementId = agreementId,
                    genericAgreements = listOf(
                        GenericAgreement(
                            id = agreementId,
                            fromDate = null,
                            toDate = null,
                            basePremium = Money.of(BigDecimal.TEN, "SEK"),
                            certificateUrl = null,
                            address = null,
                            numberCoInsured = null,
                            squareMeters = null
                        )
                    ),
                    createdAt = Instant.now()
                )
            )
        )
        every { paymentServiceClient.getDirectDebitStatusByMemberId(memberId) } returns ResponseEntity.ok(
            DirectDebitStatusDTO(memberId, true, DirectDebitStatus.ACTIVATED)
        )
        val response = client.get("/v1/members/$memberId").assert2xx().body<Map<String, Any>>()
        assertThat(response["memberId"]).isEqualTo(memberId)
    }

    @Test
    fun `can use regular insurance endpoint with external member id`() {
        val externalMemberId = UUID.randomUUID()
        val memberId = "123456"
        val agreementId = UUID.randomUUID()
        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(
            listOf(
                Contract(
                    id = UUID.randomUUID(),
                    holderMemberId = memberId,
                    masterInception = LocalDate.now(),
                    status = ContractStatus.ACTIVE,
                    terminationDate = null,
                    currentAgreementId = agreementId,
                    genericAgreements = listOf(
                        GenericAgreement(
                            id = agreementId,
                            fromDate = null,
                            toDate = null,
                            basePremium = Money.of(BigDecimal.TEN, "SEK"),
                            certificateUrl = null,
                            address = null,
                            numberCoInsured = null,
                            squareMeters = null
                        )
                    ),
                    createdAt = Instant.now()
                )
            )
        )

        every { paymentServiceClient.getDirectDebitStatusByMemberId(memberId) } returns ResponseEntity.ok(
            DirectDebitStatusDTO(memberId, true, DirectDebitStatus.ACTIVATED)
        )

        externalMemberRepository.save(
            ExternalMember(
                id = externalMemberId,
                memberId = memberId,
                partner = Partner.HEDVIG
            )
        )

        val response = client.get("/v1/members/$memberId").assert2xx().body<Map<String, Any>>()
        assertThat(response["memberId"]).isEqualTo(memberId)
    }

    @Test
    fun `retrieving member info returns not found if no member for external member id`() {
        client.get("/v1/members/${UUID.randomUUID()}").assertStatus(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `can successfully convert memberId to externalMemberId`() {
        val memberId = "123456"
        val agreementId = UUID.randomUUID()
        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(
            listOf(
                Contract(
                    id = UUID.randomUUID(),
                    holderMemberId = memberId,
                    masterInception = LocalDate.now(),
                    status = ContractStatus.ACTIVE,
                    terminationDate = null,
                    currentAgreementId = agreementId,
                    genericAgreements = listOf(
                        GenericAgreement(
                            id = agreementId,
                            fromDate = null,
                            toDate = null,
                            basePremium = Money.of(BigDecimal.TEN, "SEK"),
                            certificateUrl = null,
                            address = null,
                            numberCoInsured = null,
                            squareMeters = null
                        )
                    ),
                    createdAt = Instant.now()
                )
            )
        )

        every { paymentServiceClient.getDirectDebitStatusByMemberId(memberId) } returns ResponseEntity.ok(
            DirectDebitStatusDTO(memberId, true, DirectDebitStatus.ACTIVATED)
        )

        val response = client.post("/v1/members/$memberId/to-external-member-id")
            .assert2xx()
            .body<Map<String, Any>>()

        assertThat(response["id"]).isNotNull
    }

    @Test
    fun `member id that is not connected to a contract should not be converted`() {
        val memberId = "123456"
        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        every { productPricingClient.getTrialByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        client.post("/v1/members/123456/to-external-member-id").assertStatus(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `existing external member should be returned when attempting to create new external user`() {
        val memberId = "123456"
        val externalMemberId = UUID.randomUUID()

        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        every { productPricingClient.getTrialByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        externalMemberRepository.save(ExternalMember(externalMemberId, memberId, Partner.HEDVIG))

        val response = client.post("/v1/members/$memberId/to-external-member-id")
            .assert2xx()
            .body<Map<String, Any>>()
        assertThat(response["id"]).isEqualTo("$externalMemberId")
    }

    @Test
    fun `can get insurance info for trial members`() {
        val externalMemberId = UUID.randomUUID()
        val memberId = "123456"
        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        every { productPricingClient.getTrialByMemberId(memberId) } returns ResponseEntity.ok(
            listOf(
                TrialDto(
                    id = UUID.randomUUID(),
                    memberId = memberId,
                    fromDate = LocalDate.now(),
                    toDate = LocalDate.now().plusDays(30),
                    type = TrialType.SE_APARTMENT_BRF,
                    address = TrialDto.Address(
                        street = "Teststreet 1",
                        city = "Testtown",
                        zipCode = "12345",
                        livingSpace = null,
                        apartmentNo = null,
                        floor = null
                    ),
                    partner = Partner.HEDVIG.name
                )
            )
        )
        every { paymentServiceClient.getDirectDebitStatusByMemberId(memberId) } returns ResponseEntity.ok(
            DirectDebitStatusDTO(memberId, true, DirectDebitStatus.ACTIVATED)
        )
        externalMemberRepository.save(ExternalMember(externalMemberId, memberId, Partner.HEDVIG))

        val response = client.get("/v1/members/$externalMemberId")
            .assert2xx()
            .body<Map<String, Any>>()
        assertThat(response["memberId"]).isEqualTo(memberId)
        assertThat(response["insuranceStatus"]).isEqualTo(InsuranceStatus.ACTIVE.name)
        assertThat(response["insurancePremium"]).isEqualToComparingFieldByField(mapOf("amount" to "0.00", "currency" to "SEK"))
        assertThat(response["inceptionDate"]).isEqualTo(LocalDate.now().toString())
        assertThat(response["paymentConnected"]).isEqualTo(true)
    }

    @Test
    fun `can get extended insurance info for trial members`() {
        val externalMemberId = UUID.randomUUID()
        val memberId = "123456"
        every { productPricingClient.getContractsByMemberId(memberId) } returns ResponseEntity.ok(emptyList())
        every { productPricingClient.getTrialByMemberId(memberId) } returns ResponseEntity.ok(
            listOf(
                TrialDto(
                    id = UUID.randomUUID(),
                    memberId = memberId,
                    fromDate = LocalDate.now(),
                    toDate = LocalDate.now().plusDays(30),
                    type = TrialType.SE_APARTMENT_BRF,
                    address = TrialDto.Address(
                        street = "Teststreet 1",
                        city = "Testtown",
                        zipCode = "12345",
                        livingSpace = 45,
                        apartmentNo = null,
                        floor = null
                    ),
                    partner = Partner.HEDVIG.name
                )
            )
        )
        every { paymentServiceClient.getDirectDebitStatusByMemberId(memberId) } returns ResponseEntity.ok(
            DirectDebitStatusDTO(memberId, true, DirectDebitStatus.ACTIVATED)
        )
        externalMemberRepository.save(ExternalMember(externalMemberId, memberId, Partner.HEDVIG))

        val response = client.get("/v1/members/$externalMemberId/extended")
            .assert2xx()
            .body<Map<String, Any>>()
        assertThat(response["isTrial"]).isEqualTo(true)
        assertThat(response["insuranceStatus"]).isEqualTo(InsuranceStatus.ACTIVE.name)
        assertThat(response["insurancePremium"]).isEqualTo(mapOf("amount" to "0.00", "currency" to "SEK"))
        assertThat(response["inceptionDate"]).isEqualTo(LocalDate.now().toString())
        assertThat(response["terminationDate"]).isEqualTo(LocalDate.now().plusDays(30).toString())
        assertThat(response["paymentConnected"]).isEqualTo(true)
        assertThat(response["paymentConnectionStatus"]).isEqualTo(DirectDebitStatus.ACTIVATED.toString())
        assertThat(response["certificateUrl"]).isEqualTo(null)
        assertThat(response["numberCoInsured"]).isEqualTo(null)
        assertThat(response["insuranceAddress"]).isEqualToComparingFieldByField(
            mapOf(
                "street" to "Teststreet 1",
                "postalCode" to "12345"
            )
        )
        assertThat(response["squareMeters"]).isEqualTo(45)
    }
}
