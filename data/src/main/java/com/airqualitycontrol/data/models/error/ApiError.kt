package com.airqualitycontrol.data.models.error

import com.airqualitycontrol.data.utils.BaseMapper
import com.airqualitycontrol.domain.entities.exception.ApiErrorException

data class ApiError(
    val code: Int?,
    val errorSchema: HttpErrorSchema?
): BaseMapper<ApiErrorException> {
    override fun mapToDomain() =
        ApiErrorException(
            code = code,
            error = errorSchema?.error,
            errorDescription = errorSchema?.errorDescription
        )
}
