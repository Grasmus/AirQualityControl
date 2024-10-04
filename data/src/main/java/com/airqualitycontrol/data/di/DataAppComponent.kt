package com.airqualitycontrol.data.di

import com.airqualitycontrol.domain.repositories.AirQualityRepository
import dagger.Component
import javax.inject.Scope

@Scope
@Retention
annotation class DataScope

@DataScope
@Component(
    modules = [
        ApiModule::class,
        RepositoriesModule::class,
        SourcesModule::class,
        UtilsModule::class
    ]
)
interface DataAppComponent {
    fun getAirQualityRepository(): AirQualityRepository
}
