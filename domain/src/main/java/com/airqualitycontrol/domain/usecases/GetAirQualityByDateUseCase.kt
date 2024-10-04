package com.airqualitycontrol.domain.usecases

import com.airqualitycontrol.domain.entities.AirQuality
import com.airqualitycontrol.domain.repositories.AirQualityRepository
import java.util.Date
import javax.inject.Inject

class GetAirQualityByDateUseCase @Inject constructor(
    private val airQualityRepository: AirQualityRepository
): BaseUseCase<List<AirQuality>, GetAirQualityByDateUseCase.Params>() {

    override suspend fun execute(params: Params?) =
        airQualityRepository.getAirQualityByDate(params?.date)

    data class Params(val date: Date)
}
