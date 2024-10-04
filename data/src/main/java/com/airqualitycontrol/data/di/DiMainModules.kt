package com.airqualitycontrol.data.di

import com.airqualitycontrol.data.repositories.AirQualityRepositoryImpl
import com.airqualitycontrol.data.sources.AirQualityDataSource
import com.airqualitycontrol.data.sources.AirQualityDataSourceImpl
import com.airqualitycontrol.data.utils.ExceptionHandler
import com.airqualitycontrol.data.utils.ExceptionHandlerImpl
import com.airqualitycontrol.domain.repositories.AirQualityRepository
import dagger.Binds
import dagger.Module

@Module
abstract class SourcesModule {
    @Binds
    abstract fun bindAirQualityDataSource(
        airQualityDataSourceImpl: AirQualityDataSourceImpl
    ): AirQualityDataSource
}

@Module
abstract class RepositoriesModule {
    @Binds
    abstract fun bindAAirQualityRepository(
        airQualityRepositoryImpl: AirQualityRepositoryImpl
    ): AirQualityRepository
}

@Module
abstract class UtilsModule {
    @Binds
    abstract fun bindExceptionHandler(
        exceptionHandlerImpl: ExceptionHandlerImpl
    ): ExceptionHandler
}

