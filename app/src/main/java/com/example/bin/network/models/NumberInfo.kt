package com.example.bin.network.models

import kotlinx.serialization.Serializable

@Serializable
data class NumberInfo (
    val length: Int? = null,
    val luhn: Boolean? = null
)