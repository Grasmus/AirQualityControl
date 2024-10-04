package com.airqualitycontrol.domain.usecases

import com.airqualitycontrol.domain.entities.AirQuality
import com.airqualitycontrol.domain.repositories.AirQualityRepository
import javax.inject.Inject

class GetPeriodAirQualityUseCase @Inject constructor(
    private val airQualityRepository: AirQualityRepository
): BaseUseCase<List<AirQuality>, GetPeriodAirQualityUseCase.Params>() {

    override suspend fun execute(params: Params?) =
        airQualityRepository.getPeriodAirQualityData(params?.days)

    data class Params(val days: Int)
}
