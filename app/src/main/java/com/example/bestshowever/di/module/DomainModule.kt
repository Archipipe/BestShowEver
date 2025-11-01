package com.example.bestshowever.di.module

import com.example.bestshowever.data.CharactersRepositoryImpl
import com.example.bestshowever.di.scope.ApplicationScope
import com.example.bestshowever.domain.CharactersRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @ApplicationScope
    @Binds
    fun bindRepositoryImpl(impl: CharactersRepositoryImpl): CharactersRepository
}