package com.example.bestshowever.presentation.charaters_feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestshowever.domain.CharactersRepository
import com.example.bestshowever.domain.entity.CharactersState
import com.example.bestshowever.domain.entity.SearchFilter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharactersFeedViewModel @Inject constructor(
    private val repository: CharactersRepository
): ViewModel() {
    val characterState = repository.getCharactersState()
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = CharactersState.Initial  )



    fun loadNextPage(){
        viewModelScope.launch {
            repository.loadNextPage()
        }
    }

    fun changeSearchFilter(searchFilter: SearchFilter){
        viewModelScope.launch {
            Log.d("changeSearchFilter","changeSearchFilter")
            repository.changeSearchFilter(searchFilter)
            repository.loadNextPage()
        }
    }

    fun refresh(){
        viewModelScope.launch {
            repository.refresh()
        }
    }

    fun changeLikeStatus(characterId: Int){
        viewModelScope.launch {
            repository.toggleFavorite(characterId)
        }
    }

}