package com.hedvig.rapio.externalservices.memberService

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.externalservices.memberService.dto.AttachTrialInsuranceRequest
import com.hedvig.rapio.externalservices.memberService.dto.CreateMemberRequest
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import com.hedvig.rapio.externalservices.memberService.dto.StartOnboardingWithSSNRequest
import com.hedvig.rapio.externalservices.memberService.dto.UpdateContactInformationRequest
import com.hedvig.rapio.externalservices.memberService.model.Address
import com.hedvig.rapio.externalservices.memberService.model.NewMemberInfo
import com.hedvig.rapio.util.InternalServerError
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import com.hedvig.rapio.externalservices.memberService.dto.Address as DtoAddress

@Service
class MemberService(
    val memberServiceClient: MemberServiceClient
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
                }.", ex
            )
        }
        return false
    }

    fun createMemberWithTrialInsurance(
        language: String,
        partner: Partner,
        fromDate: LocalDate,
        newMemberInfo: NewMemberInfo
    ) : Long {
        val memberId = memberServiceClient.createMember(
            CreateMemberRequest(language, partner.toString())
        ).bodyOrNull()?.memberId ?: throw InternalServerError

        memberServiceClient.startOnboardingWithSSN(
            memberId,
            StartOnboardingWithSSNRequest(newMemberInfo.personalNumber)
        ).bodyOrNull() ?: throw InternalServerError

        memberServiceClient.finalizeOnboarding(
            memberId,
            newMemberInfo.toUpdateRequest()
        ).bodyOrNull() ?: throw InternalServerError

        memberServiceClient.attachTemporaryInsurance(
            AttachTrialInsuranceRequest(
                fromDate = fromDate,
                toDate = fromDate.plusMonths(1),
                ownership = newMemberInfo.ownership,
                partner = partner
            )
        ).bodyOrNull() ?: throw InternalServerError

        return memberId
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}

private fun <T> ResponseEntity<T>.bodyOrNull(): T? =
    if (statusCode.is2xxSuccessful && hasBody()) body!! else null

private fun NewMemberInfo.toUpdateRequest() =
    UpdateContactInformationRequest(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        address = address.toDto(),
        birthDate = birthDate
    )

private fun Address.toDto(): DtoAddress =
    DtoAddress(street, city, zipCode, apartmentNo, floor)