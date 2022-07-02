package com.cucumber.aps

import com.google.gson.JsonParser
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.io.InputStreamReader
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

    @OptIn(ExperimentalSerializationApi::class)
    fun init(): OK? {
        val url = URL(protocol + fqdn + initEndpoint)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.addRequestProperty("Key", license)
        val responseCode = connection.responseCode

        return if (responseCode != HttpURLConnection.HTTP_OK) {
            onFailureCallback()
            null
        } else Json.decodeFromStream(connection.content as InputStream)
    }

    abstract fun verify()

    abstract fun verify(token: String)
}