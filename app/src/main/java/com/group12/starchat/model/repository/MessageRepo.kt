package com.group12.starchat.model.repository

import io.fusionauth.jwt.Signer
import io.fusionauth.jwt.domain.JWT
import io.fusionauth.jwt.hmac.HMACSigner
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MessageRepo {
    fun GenerateUserToken(): String {
        val accessKey = "auzNN7V0aB30poSilNi15HCiE"

        val signer: Signer = HMACSigner.newSHA256Signer(accessKey)

        val jwt = JWT()
            .addClaim("data", "flow")
            .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
            .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusHours(2))

        val token = JWT.getEncoder().encode(jwt, signer)

        return token
    }
}