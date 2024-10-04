package com.airqualitycontrol.feature

import android.app.Application
import com.airqualitycontrol.common.di.ApplicationComponent
import com.airqualitycontrol.common.di.DaggerApplicationComponent
import com.airqualitycontrol.data.di.DaggerDataAppComponent

class AirQualityApp: Application() {
    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        initDI()
    }

    private fun initDI() {
        appComponent = DaggerApplicationComponent
            .factory()
            .create(
                DaggerDataAppComponent.create()
            )
    }
}
