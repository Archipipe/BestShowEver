package com.example.bestshowever.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bestshowever.di.key.ViewModelKey
import com.example.bestshowever.presentation.character_detailed.CharacterDetailViewModel
import com.example.bestshowever.presentation.charaters_feed.CharactersFeedViewModel
import com.example.bestshowever.presentation.factory.ViewModelFactory
import com.example.bestshowever.presentation.favorite_characters.FavoriteCharactersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule{
    @IntoMap
    @ViewModelKey(CharactersFeedViewModel::class)
    @Binds
    fun bindCharactersFeedViewModel(viewModel: CharactersFeedViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CharacterDetailViewModel::class)
    @Binds
    fun bindCharacterDetailViewModel(viewModel: CharacterDetailViewModel): ViewModel

    @IntoMap
    @ViewModelKey(FavoriteCharactersViewModel::class)
    @Binds
    fun bindFavoriteCharactersViewModel(viewModel: FavoriteCharactersViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}