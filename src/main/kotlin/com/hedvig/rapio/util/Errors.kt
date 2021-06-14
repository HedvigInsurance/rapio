package com.hedvig.rapio.util

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException

fun internalServerError() = HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
fun forbidden() = HttpServerErrorException(HttpStatus.FORBIDDEN, "Forbidden")