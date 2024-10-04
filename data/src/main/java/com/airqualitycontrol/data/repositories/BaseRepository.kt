package com.airqualitycontrol.data.repositories

import com.airqualitycontrol.data.utils.ExceptionHandler
import javax.inject.Inject

abstract class BaseRepository {

    @Inject
    lateinit var exceptionHandler: ExceptionHandler

    protected suspend fun <RESULT> safeExecuteSuspend(call: suspend () -> RESULT) =
        try {
            call()
        } catch (throwable: Throwable) {
            throw exceptionHandler.getError(throwable)
        }
}
