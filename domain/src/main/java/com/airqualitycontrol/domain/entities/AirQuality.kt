package com.airqualitycontrol.domain.entities

import java.util.Date

data class AirQuality(
    val date: Date,
    val temperature: Double?,
    val humidity: Double?,
    val airQualityScore: Double?,
    val dustConcentration: Double?,
    val gasLeaks: Boolean?,
    val pressure: Double?
)
