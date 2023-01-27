package com.example.bin.network.models

import kotlinx.serialization.Serializable

@Serializable
data class CountryInfo (
    val numeric: String = "?",
    val alpha2: String = "?",
    val name: String = "?",
    val emoji: String = "?",
    val currency: String = "?",
    val latitude: Int? = null,
    val longitude: Int? = null,
)