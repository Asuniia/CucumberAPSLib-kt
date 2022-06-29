package com.cucumber.aps

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ClassicAuth(
    license: String,
    protocol: String,
    fqdn: String,
    timer: Int,
    onSuccessCallback: () -> Unit,
    onFailureCallback: () -> Unit
) : AbstractAuth(
    license,
    protocol,
    fqdn,
    timer,
    onSuccessCallback,
    onFailureCallback
) {
    val verifyEndpoint = "/api/v1/auth/verify"

    override fun verify() {
        val response = init()

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val url = URL(protocol + fqdn + verifyEndpoint + "?token=${response.token}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) onFailureCallback()
                else onSuccessCallback()
            }
        }, 0, timer.toLong())
    }
}

