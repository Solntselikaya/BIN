package com.example.bin.network.models

import kotlinx.serialization.Serializable

@Serializable
data class BankInfo (
    val name: String = "?",
    val url: String = "?",
    val phone: String = "?",
    val city: String = "?"
)