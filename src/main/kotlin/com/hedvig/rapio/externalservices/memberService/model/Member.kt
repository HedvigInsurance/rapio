package com.hedvig.rapio.externalservices.memberService.model

import java.time.Instant
import java.time.LocalDate

class Member(
    val memberId: Long? = null,
    val status: String? = null,
    val ssn: String? = null,
    val gender: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val street: String? = null,
    val floor: Int? = null,
    val apartment: String? = null,
    val city: String? = null,
    val zipCode: String? = null,
    val country: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val birthDate: LocalDate? = null,
    val signedOn: Instant? = null,
    val createdOn: Instant? = null,
    val fraudulentStatus: String? = null,
    val fraudulentDescription: String? = null,
    val acceptLanguage: String? = null,
    val traceMemberInfo: MutableList<Any> = ArrayList()
)