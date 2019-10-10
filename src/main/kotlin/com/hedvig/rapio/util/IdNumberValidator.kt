package com.hedvig.rapio.util

import arrow.core.None
import arrow.core.Option
import arrow.core.Some


class IdNumberValidator {


    companion object {
        fun validate(idNumber: String): Option<ValidIdNumber> {
            var value = idNumber

            // Check for nulls and false lengths
            if (value.isEmpty() || value.length < 10) {
                return None
            }

            try {
                // Remove dash and plus
                value = value.trim { it <= ' ' }.replace("-", "").replace("+", "")

                var century = ""

                // Remove century and check number
                if (value.length == 12) {
                    century = value.substring(0, 2)
                    value = value.substring(2, 12)
                } else if (value.length == 10) {
                    value = value.substring(0, 10)
                } else {
                    return None
                }
                // Remove check number
                val check = Integer.parseInt(value.substring(9, 10))
                val sValue = value.substring(0, 9)

                var result = 0

                // Calculate check number
                var i = 0
                val len = sValue.length
                while (i < len) {
                    var tmp = Integer.parseInt(sValue.substring(i, i + 1))

                    if (i % 2 == 0) {
                        tmp = tmp * 2
                    }

                    result += if (tmp > 9) {
                        1 + tmp % 10
                    } else {
                        tmp
                    }
                    i++
                }

                val isValid = (check + result) % 10 == 0
                val isSSN = Integer.parseInt(value.substring(2, 4), 10) < 13 && Integer.parseInt(value.substring(4, 6), 10) < 32
                val isCoOrdinationNumber = Integer.parseInt(value.substring(4, 6), 10) > 60
                val isMale = Integer.parseInt(value.substring(8, 9)) % 2 != 0
                val isCompany = Integer.parseInt(value.substring(2, 4), 10) >= 20

                return Some(ValidIdNumber(if (isSSN) century + value else value, isValid, isSSN, isCoOrdinationNumber, isMale, isCompany))
            } catch (ex: NumberFormatException) {
                ex.printStackTrace()
            }

            return None
        }
    }
}

data class ValidIdNumber (val idno:String, val valid:Boolean, val ssn: Boolean, val coOrdinationNumber:Boolean, val male:Boolean, val company: Boolean)
