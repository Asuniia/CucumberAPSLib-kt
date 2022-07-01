package com.cucumber.aps

import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


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

        val executor = Executors.newScheduledThreadPool(1)

        executor.scheduleAtFixedRate({
            val url = URL(protocol + fqdn + verifyEndpoint + "?token=${response.token}")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) onFailureCallback()
            else onSuccessCallback()
        },0,timer.toLong(), TimeUnit.MILLISECONDS)
    }

    override fun verify(token: String) {
        verify()
    }
}

