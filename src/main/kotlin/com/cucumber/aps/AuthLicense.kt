package com.cucumber.aps

class AuthLicense(val license: String) {

    private val protocol = "https://"
    private val fqdn = "aktech.fr"

    private var mode: AuthMode = AuthMode.CSAP

    private var timer: Int = Int.MAX_VALUE
    private var onSuccessCallback = { }
    private var onFailureCallback = { }

    fun useMode(mode: AuthMode): AuthLicense {
        this.mode = mode
        return this
    }

    fun onSuccess(callback: () -> Unit): AuthLicense {
        this.onSuccessCallback = callback
        return this
    }

    fun onFailure(callback: () -> Unit): AuthLicense {
        this.onFailureCallback = callback
        return this
    }

    fun each(timer: Int): AuthLicense {
        this.timer = timer
        return this
    }

    fun verify() {
        val constructor = when (this.mode) {
            AuthMode.CSAP -> ::CSAPAuth
            AuthMode.Classic -> ::ClassicAuth
        }

        val auth = constructor(
            this.license,
            this.protocol,
            this.fqdn,
            this.timer,
            this.onSuccessCallback,
            this.onFailureCallback
        )

        auth.verify()
    }
}