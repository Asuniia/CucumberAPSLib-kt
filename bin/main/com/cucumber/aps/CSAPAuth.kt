package com.cucumber.aps

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*

class CSAPAuth(
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

    private fun getCSAPUrl(token: String): String {
        return protocol + fqdn + "/api/v1/auth/csap/login?token=${token}"
    }

    private fun getCSAPToken(token: String): String {
        val url = URL(protocol + fqdn + "/api/v1/auth/csap/callback?token=${token}")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Thread.sleep(100)
            return getCSAPToken(token)
        }

        return Json.decodeFromString<OK>(connection.responseMessage).token
    }

    private fun verify(token: String) {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val url = URL(protocol + fqdn + "/api/v1/auth/csap/endpoint")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.addRequestProperty("Key", token)
                val responseCode = connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) onFailureCallback()
                else onSuccessCallback()
                return Json.decodeFromString<OK>(connection.responseMessage)
            }
        }, 0, timer.toLong())
    }
}