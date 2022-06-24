package com.cucumber.aps

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

abstract class AbstractAuth(
    val license: String,
    val protocol: String,
    val fqdn: String,
    val timer: Int,
    val onSuccessCallback: () -> Unit,
    val onFailureCallback: () -> Unit
) {

    private val initEndpoint: String = "/api/v1/auth/init"

    @kotlinx.serialization.Serializable
    data class OK(
        val token: String
    )

    fun init(): OK {
        val url = URL(protocol + fqdn + initEndpoint)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.addRequestProperty("Key", license)
        val responseCode = connection.responseCode

        if (responseCode != HttpURLConnection.HTTP_OK) {
            onFailureCallback()

            throw IllegalAccessError()
        }

        return Json.decodeFromString(connection.responseMessage)
    }

    abstract fun verify()
}