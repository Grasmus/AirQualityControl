package com.airqualitycontrol.domain.entities.exception

data class ApiErrorException(
    val code: Int?,
    val error: String?,
    val errorDescription: String?
): Exception()
