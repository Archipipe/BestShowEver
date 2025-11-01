package com.example.bestshowever.presentation

import android.app.Application
import com.example.bestshowever.di.component.DaggerApplicationComponent

class MainApplication: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }

}