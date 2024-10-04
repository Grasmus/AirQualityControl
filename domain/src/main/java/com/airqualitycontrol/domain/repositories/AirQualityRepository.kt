package com.airqualitycontrol.domain.repositories

import com.airqualitycontrol.domain.entities.AirQuality
import java.util.Date

interface AirQualityRepository {
    suspend fun getCurrentAirQualityData(): AirQuality
    suspend fun getPeriodAirQualityData(days: Int?): List<AirQuality>
    suspend fun getAirQualityByDate(date: Date?): List<AirQuality>
}
