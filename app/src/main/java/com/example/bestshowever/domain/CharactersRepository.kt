package com.example.bestshowever.domain

import com.example.bestshowever.domain.entity.Character
import com.example.bestshowever.domain.entity.CharactersState
import com.example.bestshowever.domain.entity.SearchFilter
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    fun getCharactersState(): Flow<CharactersState>

    suspend fun loadNextPage()

    fun changeSearchFilter(searchFilter: SearchFilter)

    suspend fun refresh()

    suspend fun getCharacterById(id: Int): Character?

    suspend fun getFavoriteCharacters(): List<Character>

    suspend fun toggleFavorite(id: Int)


}