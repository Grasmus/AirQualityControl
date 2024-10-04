package com.airqualitycontrol.domain.usecases

import com.airqualitycontrol.domain.entities.exception.ApiErrorException
import com.airqualitycontrol.domain.entities.exception.ConnectionErrorException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseUseCase<RESULT, PARAMS>: UseCase<RESULT, PARAMS> {
    operator fun invoke(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        result: ResultCallbacks<RESULT>,
        params: PARAMS? = null
    ): Job {
        return scope.launch(dispatcher) {
            result.onLoading?.invoke(true)

            try {
                val executeResult = execute(params)

                result.onSuccess?.invoke(executeResult)
                result.onLoading?.invoke(false)
            } catch (coroutineJobCancellationException: CancellationException) {
                throw coroutineJobCancellationException
            } catch (throwable: Throwable) {
                when (throwable) {
                    is ConnectionErrorException -> result.onConnectionError?.invoke(throwable)
                    is ApiErrorException -> result.onError?.invoke(throwable)
                    else -> result.onUnexpectedError?.invoke(throwable)
                }

                result.onLoading?.invoke(false)
            }
        }
    }
}

class ResultCallbacks<RESULT>(
    val onSuccess: ((RESULT) -> Unit)? = null,
    val onLoading: ((Boolean) -> Unit)? = null,
    val onError: ((ApiErrorException) -> Unit)? = null,
    val onConnectionError: ((Throwable) -> Unit)? = null,
    val onUnexpectedError: ((Throwable) -> Unit)? = null
)

interface UseCase<RESULT, PARAMS> {
    suspend fun execute(params: PARAMS?): RESULT
}
