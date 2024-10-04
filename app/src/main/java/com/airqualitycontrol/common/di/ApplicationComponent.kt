package com.airqualitycontrol.common.di

import com.airqualitycontrol.data.di.DataAppComponent
import com.airqualitycontrol.feature.graph.ChartFragment
import com.airqualitycontrol.feature.home.HomeFragment
import dagger.Component
import javax.inject.Scope

@Scope
@Retention
annotation class ApplicationScope

@ApplicationScope
@Component(
    dependencies = [
        DataAppComponent::class
    ]
)
interface ApplicationComponent {

    fun inject(chartFragment: ChartFragment)
    fun inject(homeFragment: HomeFragment)

    @Component.Factory
    interface Factory {
        fun create(dataAppComponent: DataAppComponent): ApplicationComponent
    }
}