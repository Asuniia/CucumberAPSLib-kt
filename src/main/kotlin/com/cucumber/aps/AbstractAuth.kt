package com.cucumber.aps

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors


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

    data class Error(
        val message: String,
        val code: ErrorCode,
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

        val result = BufferedReader(InputStreamReader(connection.inputStream))
            .lines().collect(Collectors.joining("\n"))

        return Json.decodeFromString(result)
    }

    abstract fun verify()
}