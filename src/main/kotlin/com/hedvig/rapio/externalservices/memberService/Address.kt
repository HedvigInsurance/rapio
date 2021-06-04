package com.hedvig.rapio.externalservices.memberService

data class Address(
    var street: String? = null,
    var city: String? = null,
    var zipCode: String? = null,
    var apartmentNo: String? = null,
    var floor: Int? = null
)
