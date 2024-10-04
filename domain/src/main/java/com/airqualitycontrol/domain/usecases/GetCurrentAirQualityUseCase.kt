package com.airqualitycontrol.domain.usecases

import com.airqualitycontrol.domain.entities.AirQuality
import com.airqualitycontrol.domain.repositories.AirQualityRepository
import javax.inject.Inject

class GetCurrentAirQualityUseCase @Inject constructor(
    private val airQualityRepository: AirQualityRepository
): BaseUseCase<AirQuality, Unit>() {

    override suspend fun execute(params: Unit?) =
        airQualityRepository.getCurrentAirQualityData()
}
