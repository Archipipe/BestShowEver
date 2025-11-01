package com.example.bestshowever.presentation.favorite_characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestshowever.domain.CharactersRepository
import com.example.bestshowever.domain.entity.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteCharactersViewModel @Inject constructor(
    private val repository: CharactersRepository
) : ViewModel() {

    private val _characterState = MutableStateFlow<CharactersState>(CharactersState.Loading)
    val characterState: StateFlow<CharactersState> = _characterState.asStateFlow()

    fun loadCharacters() {
        viewModelScope.launch {
            _characterState.value = CharactersState.Loading

            try {
                val characters = repository.getFavoriteCharacters()
                _characterState.value = CharactersState.Success(characters)
            } catch (e: Exception) {
                _characterState.value = CharactersState.Error(
                    "Failed to load character: ${e.message}"
                )
            }
        }
    }

    fun toggleFavorite(characterId: Int){
        viewModelScope.launch {
            repository.toggleFavorite(characterId)
        }
    }

    sealed class CharactersState {
        object Loading : CharactersState()
        data class Success(val characters: List<Character>) : CharactersState()
        data class Error(val message: String) : CharactersState()
    }

}

