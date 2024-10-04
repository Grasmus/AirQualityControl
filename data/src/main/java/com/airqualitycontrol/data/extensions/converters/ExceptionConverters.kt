package com.airqualitycontrol.data.extensions.converters

import com.airqualitycontrol.data.models.error.ApiError
import com.airqualitycontrol.data.models.error.HttpErrorSchema
import com.airqualitycontrol.domain.entities.exception.ApiErrorException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

fun HttpException.toApiException(): ApiErrorException? {
    return this.response()?.errorBody()?.string()?.let { rawBody ->
        val jsonBuilder = Json {
            ignoreUnknownKeys = true
        }

        ApiError(
            this.code(),
            jsonBuilder.decodeFromString<HttpErrorSchema>(rawBody)
        ).mapToDomain()
    }
}
