package com.cucumber.aps

class AuthLicense(val license: String) {

    private val protocol = "http://"
    private val fqdn = "127.0.0.1:8000"
    private val endpoint = "/api/v1/verify"

    private var mode: AuthMode = AuthMode.CSAP

    private var timer: Int = Int.MAX_VALUE
    private var onSuccessCallback = {  }
    private var onFailureCallback = {  }

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

    fun each(timer : Int): AuthLicense {
        this.timer = timer
        return this
    }

    fun verify() {
        val auth: AbstractAuth = when (this.mode) {
            AuthMode.CSAP -> CSAPAuth()
            AuthMode.Classic -> ClassicAuth()
        }

        auth.verify(
            this.license,
            this.protocol,
            this.fqdn,
            this.endpoint,
            this.timer,
            this.onSuccessCallback,
            this.onFailureCallback,
        )
    }
}