package com.airqualitycontrol.data.models.response

import com.airqualitycontrol.data.utils.BaseMapper
import com.airqualitycontrol.domain.entities.AirQuality
import com.google.gson.annotations.SerializedName
import java.util.Date

data class AirQualityResponse(
    @SerializedName("datetime") val date: Date,
    @SerializedName("temp") val temperature: Double?,
    @SerializedName("humidity") val humidity: Double?,
    @SerializedName("aqi") val airQualityScore: Double?,
    @SerializedName("dustConcentration") val dustConcentration: Double?,
    @SerializedName("gasLeak") val gasLeaks: Boolean?,
    @SerializedName("pressure") val pressure: Double?
): BaseMapper<AirQuality> {
    override fun mapToDomain() =
        AirQuality(
            date = date,
            temperature = temperature,
            humidity = humidity,
            airQualityScore = airQualityScore,
            dustConcentration = dustConcentration,
            gasLeaks = gasLeaks,
            pressure = pressure
        )
}
