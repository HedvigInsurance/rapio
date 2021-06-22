package com.hedvig.rapio.util

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException

fun internalServerError(message: String? = null) = HttpServerErrorException(
    HttpStatus.INTERNAL_SERVER_ERROR,
    HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
    message?.toByteArray(Charsets.UTF_8),
    Charsets.UTF_8
)

fun forbidden(message: String? = null) = HttpServerErrorException(
    HttpStatus.FORBIDDEN,
    HttpStatus.FORBIDDEN.reasonPhrase,
    message?.toByteArray(Charsets.UTF_8),
    Charsets.UTF_8
)

fun unauthorized(message: String? = null) = HttpServerErrorException(
    HttpStatus.UNAUTHORIZED,
    HttpStatus.UNAUTHORIZED.reasonPhrase,
    message?.toByteArray(Charsets.UTF_8),
    Charsets.UTF_8
)
