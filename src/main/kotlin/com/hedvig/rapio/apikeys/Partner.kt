package com.hedvig.rapio.apikeys

enum class Partner(val role: Roles) {
    INSPLANET(role = Roles.COMPARISON),
    COMPRICER(role = Roles.COMPARISON),
    HEDVIG(role = Roles.COMPARISON),
    INSURLEY(role = Roles.COMPARISON),
    KEYSOLUTIONS(role = Roles.COMPARISON),
    SPIFF(role = Roles.COMPARISON),
    TJENSTETORGET(role = Roles.COMPARISON),
    AVY(role = Roles.INSURANCE_INFO)
}
