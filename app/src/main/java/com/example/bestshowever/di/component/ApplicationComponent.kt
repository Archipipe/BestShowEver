package com.example.bestshowever.di.component

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.bestshowever.di.module.DataModule
import com.example.bestshowever.di.module.ViewModelModule
import com.example.bestshowever.di.module.DomainModule
import com.example.bestshowever.di.scope.ApplicationScope
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [DataModule::class, ViewModelModule::class, DomainModule::class]
)
interface ApplicationComponent {

    fun inject(application: Application)

    fun getViewModelFactory(): ViewModelProvider.Factory


    @Component.Factory
    interface ApplicationComponentFactory{
        fun create(
            @BindsInstance application: Application
        ):ApplicationComponent
    }
}