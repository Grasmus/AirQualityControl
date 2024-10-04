package com.airqualitycontrol.data.models.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("HttpErrorSchema")
data class HttpErrorSchema(
    @SerialName("error") val error: String?,
    @SerialName("errorDescription") val errorDescription: String?
)
