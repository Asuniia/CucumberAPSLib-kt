package com.cucumber.aps

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.net.HttpURLConnection
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

    public fun getCSAPUrl(token: String): String {
        return protocol + fqdn + "/api/v1/auth/csap/login?token=${token}"
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun getCSAPToken(token: String): String {
        val url = URL(protocol + fqdn + "/api/v1/auth/csap/callback?token=${token}")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Can't access to CSAP API")
        }

        return Json.decodeFromStream<String>(connection.content as InputStream).toString()
    }

    override fun verify() {
        verify(license)
    }

    override fun verify(token: String) {
        val response = init()

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val url = URL(getCSAPUrl(response!!.token))
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.addRequestProperty("Key", token)
                val responseCode = connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) onFailureCallback()
                else onSuccessCallback()
            }
        }, 0, timer.toLong())
    }
}