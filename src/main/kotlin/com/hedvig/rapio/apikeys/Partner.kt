package com.hedvig.rapio.apikeys

enum class Partner(val role: Roles) {
    INSPLANET(role = Roles.COMPARISON),
    COMPRICER(role = Roles.COMPARISON),
    HEDVIG(role = Roles.COMPARISON),
    INSURLEY(role = Roles.COMPARISON),
    KEYSOLUTIONS(role = Roles.COMPARISON),
    SPIFF(role = Roles.COMPARISON),
    TJENSTETORGET(role = Roles.COMPARISON),
    AVY(role = Roles.DISTRIBUTION),
    FORSIKRINGSPORTALEN(role = Roles.COMPARISON),
    SAMLINO(role = Roles.COMPARISON),
    FINDFORSIKRING(role = Roles.COMPARISON),
    FORSIKRINGSGUIDEN(role = Roles.COMPARISON),
    KEY_HOLE(role = Roles.COMPARISON),
    COPENHAGEN_SALES(role = Roles.COMPARISON)
}
