package com.cucumber.aps

interface AbstractAuth {
    fun verify(
        license: String,
        protocol: String,
        fqdn: String,
        endpoint: String,
        timer: Int,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit,
    )
}