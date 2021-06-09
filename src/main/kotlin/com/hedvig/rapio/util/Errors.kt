package com.hedvig.rapio.util

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException

object InternalServerError : HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
object Forbidden : HttpServerErrorException(HttpStatus.FORBIDDEN, "Forbidden")