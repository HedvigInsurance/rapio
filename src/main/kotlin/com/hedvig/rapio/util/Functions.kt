package com.hedvig.rapio.util

fun <A, B, R> Pair<A?, B?>.let2(block: (A, B) -> R): R? =
    if (this.first != null && this.second != null)
        block(this.first!!, this.second!!)
    else null

fun <A, B, C, R> Triple<A?, B?, C?>.let3(block: (A, B, C) -> R): R? =
    if (this.first != null && this.second != null && this.third != null)
        block(this.first!!, this.second!!, this.third!!)
    else null