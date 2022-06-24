package com.cucumber.aps

class CSAPAuth : AbstractAuth {
    override fun verify(
        license: String,
        protocol: String,
        fqdn: String,
        endpoint: String,
        timer: Int,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        TODO("Not yet implemented")
    }
}