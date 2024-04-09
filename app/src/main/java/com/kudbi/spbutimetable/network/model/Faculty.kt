package com.example.spbutimetableonapi.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Faculty (
    @SerialName("Oid") val id: String,
    @SerialName("Alias") val alias: String,
    @SerialName("Name") val name: String
)