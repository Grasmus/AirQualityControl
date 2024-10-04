package com.airqualitycontrol.data.repositories

import com.airqualitycontrol.data.sources.AirQualityDataSource
import com.airqualitycontrol.domain.repositories.AirQualityRepository
import java.util.Date
import javax.inject.Inject

class AirQualityRepositoryImpl @Inject constructor(
    private val airQualityDataSource: AirQualityDataSource
): AirQualityRepository, BaseRepository() {
    override suspend fun getCurrentAirQualityData() =
        safeExecuteSuspend {
            airQualityDataSource.getCurrentAirQualityData()
        }

    override suspend fun getPeriodAirQualityData(days: Int?) =
        safeExecuteSuspend {
            days?.let {
                airQualityDataSource.getPeriodAirQualityData(days)
            } ?: throw Exception("Parameter days was null!")
        }

    override suspend fun getAirQualityByDate(date: Date?) =
        safeExecuteSuspend {
            date?.let {
                airQualityDataSource.getAirQualityByDate(date)
            } ?: throw Exception("Date was null!")
        }
}
