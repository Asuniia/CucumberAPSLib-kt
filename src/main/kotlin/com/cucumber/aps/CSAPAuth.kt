package com.cucumber.aps

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*
import java.util.prefs.Preferences

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

    companion object {
        @JvmField // TODO: Write javadoc
        val handleCSAPConfirmScreen: (CSAPScreenLink: String, CSAPCallbackLink: String) -> String =
            ::defaultHandleCSAPConfirmScreen

        @JvmField // TODO: Write javadoc
        val handleCSAPStoreToken: (token: String) -> Unit =
            ::defaultHandleCSAPStoreToken

        @JvmField // TODO: Write javadoc
        val handleCSAPRetrieveToken: () -> String? =
            ::defaultHandleCSAPRetrieveToken

        @JvmStatic
        fun defaultHandleCSAPConfirmScreen(CSAPScreenLink: String, CSAPCallbackLink: String): String {
            fun getCSAPToken(): String {
                val url = URL(CSAPCallbackLink)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Thread.sleep(100)
                    return getCSAPToken()
                }

                return Json.decodeFromString<OK>(connection.responseMessage).token
            }

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(CSAPScreenLink));
            }

            return getCSAPToken()
        }

        @JvmStatic
        fun defaultHandleCSAPStoreToken(token: String) {
            Preferences.userNodeForPackage(Companion::class.java)
                .put("token", token)
        }

        @JvmStatic
        fun defaultHandleCSAPRetrieveToken(): String {
            return Preferences.userNodeForPackage(Companion::class.java)
                .get("token", null)
        }
    }

    override fun verify() {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var tokenCSAP = handleCSAPRetrieveToken()

                if (tokenCSAP == null) {
                    val response = init()

                    tokenCSAP = handleCSAPConfirmScreen(
                        protocol + fqdn + "/api/v1/auth/cspa/login?token=${response.token}",
                        protocol + fqdn + "/api/v1/auth/cspa/callback?token=${response.token}",
                    )
                }

                val url = URL(protocol + fqdn + "/api/v1/auth/csap/endpoint?token=${tokenCSAP}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    val error = Json.decodeFromString<Error>(connection.responseMessage)

                    when (error.code) {
                        ErrorCode.CSAP_IP_INVALID -> this@CSAPAuth.verify()
                        else -> onFailureCallback()
                    }
                }
                else onSuccessCallback()
            }
        }, 0, timer.toLong())
    }
}