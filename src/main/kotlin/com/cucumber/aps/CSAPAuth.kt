package com.cucumber.aps

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

    override fun verify() {
        // la license est un token?
        // OUI =>
    }
}