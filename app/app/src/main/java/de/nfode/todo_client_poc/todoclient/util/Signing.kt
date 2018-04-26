package de.nfode.todo_client_poc.todoclient.util

import org.apache.commons.codec.binary.Hex
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class Signing {
    val secret = "secret"
    fun generateConnectionToken(userId: String, timestamp: String, info: String): String {
        val secretKeySpec = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        var mac: Mac? = null
        try {
            mac = Mac.getInstance("HmacSHA256")
        } catch (e: NoSuchAlgorithmException) {
        }

        try {
            mac!!.init(secretKeySpec)
        } catch (e: InvalidKeyException) {
        }

        mac!!.update(userId.toByteArray())
        mac!!.update(timestamp.toByteArray())
        val hmac = mac!!.doFinal(info.toByteArray())
        val encode = Hex().encode(hmac)
        try {
            return String(encode, StandardCharsets.UTF_8)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return ""
    }
}