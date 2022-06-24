package com.cucumber.aps

interface AbstractAuth {
    fun verify(
        license: String,
        protocol: String,
        fqdn: String,
        timer: Int,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit,
    )
}