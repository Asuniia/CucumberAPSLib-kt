package com.cucumber.aps

abstract class AbstractAuth {
    val initEndpoint : String = "/api/v1/auth/init"

    abstract fun verify(
        license: String,
        protocol: String,
        fqdn: String,
        timer: Int,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit,
    )
}