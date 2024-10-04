package com.airqualitycontrol.data.utils

import com.airqualitycontrol.data.extensions.converters.toApiException
import com.airqualitycontrol.domain.entities.exception.ConnectionErrorException
import com.airqualitycontrol.domain.entities.exception.UnexpectedErrorException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ExceptionHandlerImpl @Inject constructor(): ExceptionHandler {
    override fun getError(throwable: Throwable) =
        when (throwable) {
            is IOException -> ConnectionErrorException()
            is HttpException -> throwable.toApiException() ?: UnexpectedErrorException(throwable)
            else -> UnexpectedErrorException(throwable)
        }
}

interface ExceptionHandler {
    fun getError(throwable: Throwable): Exception
}
