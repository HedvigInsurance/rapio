package com.hedvig.rapio.members.dto

data class Address(
    val street: String,
    val city: String,
    val zipCode: String,
    val apartmentNo: String?,
    val floor: Int?
)
