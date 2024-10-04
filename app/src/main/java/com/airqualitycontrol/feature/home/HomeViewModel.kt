package com.airqualitycontrol.feature.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airqualitycontrol.common.base.viewmodel.BaseViewModel
import com.airqualitycontrol.common.constans.HPA_TO_MMHG_CONVERT_RATIO
import com.airqualitycontrol.common.utils.SingleLiveEvent
import com.airqualitycontrol.domain.entities.AirQuality
import com.airqualitycontrol.domain.entities.exception.ApiErrorException
import com.airqualitycontrol.domain.usecases.GetCurrentAirQualityUseCase
import com.airqualitycontrol.domain.usecases.ResultCallbacks
import kotlinx.coroutines.Dispatchers
import java.util.Date
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getCurrentAirQualityUseCase: GetCurrentAirQualityUseCase
): BaseViewModel() {

    val uiEvent: LiveData<UiEvent>
        get() = mutableUiEvent

    val temperature: LiveData<Double>
        get() = mutableTemperature

    val humidity: LiveData<Double>
        get() = mutableHumidity

    val airQuality: LiveData<Double>
        get() = mutableAirQuality

    val dustConcentration: LiveData<Double>
        get() = mutableDustConcentration

    val pressureInHPA: LiveData<Double>
        get() = mutablePressureInHPA

    val pressureInMmHG: LiveData<Double>
        get() = mutablePressureInMmHG

    val gasLeaks: LiveData<Boolean>
        get() = mutableGasLeaks

    val date: LiveData<Date>
        get() = mutableDate

    val error: LiveData<ApiErrorException>
        get() = mutableError

    private val mutableUiEvent = SingleLiveEvent<UiEvent>()
    private val mutableTemperature = MutableLiveData<Double>()
    private val mutableHumidity = MutableLiveData<Double>()
    private val mutableAirQuality = MutableLiveData<Double>()
    private val mutableDustConcentration = MutableLiveData<Double>()
    private val mutablePressureInHPA = MutableLiveData<Double>()
    private val mutablePressureInMmHG = MutableLiveData<Double>()
    private val mutableGasLeaks = MutableLiveData<Boolean>()
    private val mutableDate = MutableLiveData<Date>()
    private val mutableError = MutableLiveData<ApiErrorException>()

    private var isCurrentPressureInHPa = true

    init {
        getCurrentAirQuality()
    }

    fun getIsCurrentPressureInHPa() = isCurrentPressureInHPa

    fun setIsCurrentPressureINHPa(isCurrentPressureInHPa: Boolean) {
        this.isCurrentPressureInHPa = isCurrentPressureInHPa
    }

    private fun getCurrentAirQuality() {
        getCurrentAirQualityUseCase.invoke(
            scope = viewModelScope,
            dispatcher = Dispatchers.IO,
            result = ResultCallbacks(
                onSuccess = ::onGetCurrentAirQualitySuccess,
                onError = ::onGetCurrentAirQualityError,
                onLoading = ::onLoading,
                onConnectionError = ::onGetCurrentAirQualityConnectionError,
                onUnexpectedError = ::onUnexpectedError
            )
        )
    }

    private fun onGetCurrentAirQualitySuccess(airQuality: AirQuality) {
        airQuality.temperature?.let { temperature -> mutableTemperature.postValue(temperature) }
        airQuality.humidity?.let { humidity -> mutableHumidity.postValue(humidity) }
        airQuality.airQualityScore?.let { airQualityScore -> mutableAirQuality.postValue(airQualityScore) }
        airQuality.dustConcentration?.let { dustConcentration -> mutableDustConcentration.postValue(dustConcentration) }
        airQuality.pressure?.let { pressure ->
            mutablePressureInHPA.postValue(pressure)
            mutablePressureInMmHG.postValue(pressure * HPA_TO_MMHG_CONVERT_RATIO)
        }
        airQuality.gasLeaks?.let { gasLeaks -> mutableGasLeaks.postValue(gasLeaks) }
        mutableDate.postValue(airQuality.date)
    }

    private fun onUnexpectedError(throwable: Throwable) {
        mutableUiEvent.postValue(UiEvent.OnUnexpectedError)

        Log.wtf("HomeViewModel::onUnexpectedError",
            throwable.message)
    }

    private fun onGetCurrentAirQualityError(error: ApiErrorException) {
        mutableError.postValue(error)
    }

    private fun onGetCurrentAirQualityConnectionError(throwable: Throwable) {
        mutableUiEvent.postValue(UiEvent.OnConnectionError)

        formCallFlow(::getCurrentAirQuality)
    }

    sealed class UiEvent {
        data object OnConnectionError: UiEvent()

        data object OnUnexpectedError: UiEvent()
    }
}
