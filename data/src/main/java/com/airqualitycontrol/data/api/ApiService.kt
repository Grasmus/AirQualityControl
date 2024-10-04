package com.airqualitycontrol.data.api

import com.airqualitycontrol.data.models.response.AirQualityResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @GET("last-reading")
    suspend fun getCurrentAirQuality(): AirQualityResponse

    @Headers("Content-Type: application/json")
    @GET("daily-averages")
    suspend fun getPeriodAirQuality(
        @Query("days") days: Int
    ): List<AirQualityResponse>

    @Headers("Content-Type: application/json")
    @GET("hourly-readings")
    suspend fun getAirQualityByDate(
        @Query("date") date: String
    ): List<AirQualityResponse>
}
