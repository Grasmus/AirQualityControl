package com.airqualitycontrol.data.sources

import com.airqualitycontrol.data.api.ApiService
import com.airqualitycontrol.data.constans.DATE_FORMATTER_PATTERN
import com.airqualitycontrol.domain.entities.AirQuality
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

interface AirQualityDataSource {
    suspend fun getCurrentAirQualityData(): AirQuality
    suspend fun getPeriodAirQualityData(days: Int): List<AirQuality>
    suspend fun getAirQualityByDate(date: Date): List<AirQuality>
}

class AirQualityDataSourceImpl @Inject constructor(
    private val apiService: ApiService
): AirQualityDataSource {
    override suspend fun getCurrentAirQualityData() =
        apiService.getCurrentAirQuality().mapToDomain()

    override suspend fun getPeriodAirQualityData(days: Int) =
        apiService.getPeriodAirQuality(days).map {
            it.mapToDomain()
        }

    override suspend fun getAirQualityByDate(date: Date): List<AirQuality> {
        val formatter = SimpleDateFormat(DATE_FORMATTER_PATTERN, Locale.getDefault())

        return apiService.getAirQualityByDate(formatter.format(date)).map {
            it.mapToDomain()
        }
    }
}
