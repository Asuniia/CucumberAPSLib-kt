package com.cucumber.aps

import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ClassicAuth : AbstractAuth {
    override fun verify(
        license: String,
        protocol: String,
        fqdn: String,
        endpoint: String,
        timer: Int,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val url = URL(protocol + fqdn + endpoint)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.addRequestProperty("Key", license)
                val responseCode = connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) onFailureCallback()
                else onSuccessCallback()
            }
        }, 0, timer.toLong())
    }
}