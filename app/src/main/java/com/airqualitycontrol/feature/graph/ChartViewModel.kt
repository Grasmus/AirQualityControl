package com.airqualitycontrol.feature.graph

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airqualitycontrol.common.base.viewmodel.BaseViewModel
import com.airqualitycontrol.common.utils.SingleLiveEvent
import com.airqualitycontrol.domain.entities.AirQuality
import com.airqualitycontrol.domain.entities.exception.ApiErrorException
import com.airqualitycontrol.domain.usecases.GetAirQualityByDateUseCase
import com.airqualitycontrol.domain.usecases.GetPeriodAirQualityUseCase
import com.airqualitycontrol.domain.usecases.ResultCallbacks
import kotlinx.coroutines.Dispatchers
import java.util.Date
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    private val getPeriodAirQualityUseCase: GetPeriodAirQualityUseCase,
    private val getAirQualityByDateUseCase: GetAirQualityByDateUseCase
): BaseViewModel() {

    val uiEvent: LiveData<UiEvent>
        get() = mutableUiEvent

    val temperatureList: LiveData<List<Double?>>
        get() = mutableTemperatureList

    val humidityList: LiveData<List<Double?>>
        get() = mutableHumidityList

    val airQualityList: LiveData<List<Double?>>
        get() = mutableAirQualityList

    val dustConcentrationList: LiveData<List<Double?>>
        get() = mutableDustConcentrationList

    val pressureList: LiveData<List<Double?>>
        get() = mutablePressureList

    val dates: LiveData<List<Date>>
        get() = mutableDates

    val error: LiveData<ApiErrorException>
        get() = mutableError

    private val mutableUiEvent = SingleLiveEvent<UiEvent>()
    private val mutableTemperatureList = MutableLiveData<List<Double?>>()
    private val mutableHumidityList = MutableLiveData<List<Double?>>()
    private val mutableAirQualityList = MutableLiveData<List<Double?>>()
    private val mutableDustConcentrationList = MutableLiveData<List<Double?>>()
    private val mutablePressureList = MutableLiveData<List<Double?>>()
    private val mutableError = MutableLiveData<ApiErrorException>()
    private val mutableDates = MutableLiveData<List<Date>>()

    private var days: Int? = null
    private var date: Date? = null

    fun getAirQuality(days: Int) {
        this.days = days

        getPeriodAirQuality()
    }

    fun getAirQualityByDate(date: Date) {
        this.date = date

        getAirQualityByDate()
    }

    private fun getPeriodAirQuality() {
        getPeriodAirQualityUseCase.invoke(
            scope = viewModelScope,
            dispatcher = Dispatchers.IO,
            params = days?.let{ GetPeriodAirQualityUseCase.Params(it) },
            result = ResultCallbacks(
                onSuccess = ::onGetAirQualitySuccess,
                onLoading = ::onLoading,
                onError = ::onGetAirQualityError,
                onUnexpectedError = ::onUnexpectedError,
                onConnectionError = ::onGetAirQualityConnectionError
            )
        )
    }

    private fun getAirQualityByDate() {
        getAirQualityByDateUseCase.invoke(
            scope = viewModelScope,
            dispatcher = Dispatchers.IO,
            params = date?.let { GetAirQualityByDateUseCase.Params(it) },
            result = ResultCallbacks(
                onSuccess = ::onGetAirQualityByDateSuccess,
                onError = ::onGetAirQualityByDateError,
                onLoading = ::onLoading,
                onUnexpectedError = ::onUnexpectedError,
                onConnectionError = ::onGetAirQualityByDateConnectionError
            )
        )
    }

    private fun onUnexpectedError(throwable: Throwable) {
        mutableUiEvent.postValue(UiEvent.OnUnexpectedError)

        Log.wtf("GraphViewModel::onUnexpectedError",
            throwable.message)
    }

    private fun onGetAirQualitySuccess(airQualityList: List<AirQuality>) {
        mutableDates.postValue(airQualityList.map { it.date })
        mutableTemperatureList.postValue(airQualityList.map { it.temperature })
        mutableHumidityList.postValue(airQualityList.map { it.humidity })
        mutableAirQualityList.postValue(airQualityList.map { it.airQualityScore })
        mutableDustConcentrationList.postValue(airQualityList.map { it.dustConcentration })
        mutablePressureList.postValue(airQualityList.map { it.pressure })
    }

    private fun onGetAirQualityError(error: ApiErrorException) {
        mutableError.postValue(error)
    }

    private fun onGetAirQualityConnectionError(throwable: Throwable) {
        mutableUiEvent.postValue(UiEvent.OnConnectionError)

        formCallFlow(::getPeriodAirQuality)
    }

    private fun onGetAirQualityByDateSuccess(airQualityList: List<AirQuality>) {
        mutableDates.postValue(airQualityList.map { it.date })
        mutableTemperatureList.postValue(airQualityList.map { it.temperature })
        mutableHumidityList.postValue(airQualityList.map { it.humidity })
        mutableAirQualityList.postValue(airQualityList.map { it.airQualityScore })
        mutableDustConcentrationList.postValue(airQualityList.map { it.dustConcentration })
        mutablePressureList.postValue(airQualityList.map { it.pressure })
    }

    private fun onGetAirQualityByDateError(error: ApiErrorException) {
        mutableError.postValue(error)
    }

    private fun onGetAirQualityByDateConnectionError(throwable: Throwable) {
        mutableUiEvent.postValue(UiEvent.OnConnectionError)

        formCallFlow(::getAirQualityByDate)
    }

    sealed class UiEvent {
        data object OnConnectionError: UiEvent()

        data object OnUnexpectedError: UiEvent()
    }
}
