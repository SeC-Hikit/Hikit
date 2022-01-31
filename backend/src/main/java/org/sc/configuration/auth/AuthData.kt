package org.sc.configuration.auth

data class AuthData(
        val username: String,
        val realm: String,
        val instance: String,
)
