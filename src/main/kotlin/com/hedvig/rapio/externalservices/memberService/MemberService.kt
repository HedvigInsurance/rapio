package com.hedvig.rapio.externalservices.memberService

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.externalservices.memberService.dto.CreateMemberRequest
import com.hedvig.rapio.externalservices.memberService.dto.CreateTrialRequest
import com.hedvig.rapio.externalservices.memberService.dto.CreateUserRequest
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import com.hedvig.rapio.externalservices.memberService.dto.SimpleSignConnectionDto
import com.hedvig.rapio.externalservices.memberService.dto.UpdateMemberRequest
import com.hedvig.rapio.externalservices.memberService.model.NewMemberInfo
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import com.hedvig.rapio.util.forbidden
import com.hedvig.rapio.util.internalServerError
import com.neovisionaries.i18n.CountryCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MemberService(
    private val memberServiceClient: MemberServiceClient,
    private val productPricingClient: ProductPricingClient
) {

    fun isMember(memberId: String?, ssn: String?, email: String?): Boolean {
        try {
            val response = memberServiceClient.getIsMember(IsMemberRequest(memberId, ssn, email))
            if (response.statusCode.is2xxSuccessful) {
                return response.body!!
            }
        } catch (ex: Exception) {
            logger.error(
                "Error from member service when checking member status for ${
                IsMemberRequest(
                    memberId,
                    ssn,
                    email
                )
                }.",
                ex
            )
        }
        return false
    }

    fun createMemberWithTrialInsurance(
        language: String,
        countryCode: CountryCode,
        partner: Partner,
        fromDate: LocalDate,
        newMemberInfo: NewMemberInfo
    ): Long {
        val personalNumberIsTaken = memberServiceClient.getIsMember(
            IsMemberRequest(ssn = newMemberInfo.personalNumber)
        ).bodyOrNull() ?: throw internalServerError()
        if (personalNumberIsTaken) {
            throw forbidden()
        }
        val memberId = memberServiceClient.createMember(
            CreateMemberRequest(language, partner.toString())
        ).bodyOrNull()?.memberId ?: throw internalServerError()

        memberServiceClient.updateMember(
            memberId, newMemberInfo.toUpdateMemberRequest(countryCode)
        ).bodyOrNull() ?: throw internalServerError()

        memberServiceClient.createUser(
            CreateUserRequest(
                memberId.toString(),
                SimpleSignConnectionDto(
                    newMemberInfo.personalNumber,
                    countryCode
                )
            )
        ).bodyOrNull() ?: throw internalServerError()

        productPricingClient.createTrial(
            newMemberInfo.toTrialRequest(memberId, fromDate, partner)
        ).bodyOrNull() ?: throw internalServerError()

        return memberId
    }

    private fun NewMemberInfo.toTrialRequest(
        memberId: Long,
        fromDate: LocalDate,
        partner: Partner
    ) = CreateTrialRequest(
        memberId = memberId.toString(),
        fromDate = fromDate,
        type = type,
        partner = partner,
        address = CreateTrialRequest.Address(
            street = address.street,
            city = address.city,
            zipCode = address.zipCode,
            livingSpace = address.livingSpace,
            apartmentNo = address.apartmentNo,
            floor = address.floor
        )
    )

    private fun NewMemberInfo.toUpdateMemberRequest(
        language: CountryCode
    ) = UpdateMemberRequest(
        firstName = firstName,
        lastName = lastName,
        ssn = personalNumber,
        countryCode = language,
        address = UpdateMemberRequest.Address(
            street = address.street,
            city = address.city,
            zipCode = address.zipCode,
            apartmentNo = address.apartmentNo,
            floor = address.floor
        ),
        email = email,
        phoneNumber = phoneNumber,
        birthDate = birthDate
    )

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}

private fun <T> ResponseEntity<T>.bodyOrNull(): T? =
    if (statusCode.is2xxSuccessful && hasBody()) body!! else null
