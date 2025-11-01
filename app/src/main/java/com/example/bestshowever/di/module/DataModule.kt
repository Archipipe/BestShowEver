package com.example.bestshowever.di.module

import android.app.Application
import com.example.bestshowever.data.database.AppDatabase
import com.example.bestshowever.data.database.CharactersDao
import com.example.bestshowever.data.network.ApiFactory
import com.example.bestshowever.data.network.ApiService
import com.example.bestshowever.di.scope.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @ApplicationScope
    @Provides
    fun provideApiService(): ApiService{
        return ApiFactory.retrofit.create(ApiService::class.java)
    }

    @ApplicationScope
    @Provides
    fun provideCharactersDao(application: Application): CharactersDao{
        return AppDatabase.getInstance(application).charactersDao()
    }
}