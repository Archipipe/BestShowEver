package com.example.bestshowever.presentation.character_detailed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestshowever.domain.CharactersRepository
import com.example.bestshowever.domain.entity.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterDetailViewModel @Inject constructor(
    private val repository: CharactersRepository
) : ViewModel() {

    private val _characterState = MutableStateFlow<CharacterDetailState>(CharacterDetailState.Loading)
    val characterState: StateFlow<CharacterDetailState> = _characterState.asStateFlow()

    fun loadCharacter(characterId: Int) {
        viewModelScope.launch {
            _characterState.value = CharacterDetailState.Loading

            try {
                val character = repository.getCharacterById(characterId)
                if (character != null) {
                    _characterState.value = CharacterDetailState.Success(character)
                } else {
                    _characterState.value = CharacterDetailState.Error("Character not found")
                }
            } catch (e: Exception) {
                _characterState.value = CharacterDetailState.Error(
                    "Failed to load character: ${e.message}"
                )
            }
        }
    }

    sealed class CharacterDetailState {
        object Loading : CharacterDetailState()
        data class Success(val character: Character) : CharacterDetailState()
        data class Error(val message: String) : CharacterDetailState()
    }

}

