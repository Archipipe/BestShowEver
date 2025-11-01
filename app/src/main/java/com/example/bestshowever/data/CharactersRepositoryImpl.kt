package com.example.bestshowever.data

import android.app.Application
import android.util.Log
import com.example.bestshowever.data.database.CharactersDao
import com.example.bestshowever.data.helpers.isConnectedToInternet
import com.example.bestshowever.data.network.ApiService
import com.example.bestshowever.data.network.CharacterDto
import com.example.bestshowever.domain.CharactersRepository
import com.example.bestshowever.domain.entity.Character
import com.example.bestshowever.domain.entity.CharactersState
import com.example.bestshowever.domain.entity.SearchFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import retrofit2.HttpException

class CharactersRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val mapper: Mapper,
    private val charactersDao: CharactersDao,
    private val application: Application
) : CharactersRepository {

    private val _charactersState = MutableStateFlow<CharactersState>(CharactersState.Initial)
    override fun getCharactersState(): Flow<CharactersState> = _charactersState.asStateFlow()

    override suspend fun loadNextPage() {

        val currentState = _charactersState.value
        val searchQuery = (currentState as? CharactersState.Page)?.searchFilter ?: SearchFilter()

        if (currentState is CharactersState.Page &&
            (currentState.isLoading || !currentState.hasNextPage)
        ) return

        val nextPageDB = when (currentState) {
            is CharactersState.Page -> currentState.pageNumberDB + 1
            else -> 1
        }

        var nextPageNet = when (currentState) {
            is CharactersState.Page -> currentState.pageNumberNet + 1
            else -> 1
        }


        _charactersState.update { current ->
            when (current) {
                CharactersState.Initial -> CharactersState.Page(isLoading = true)
                is CharactersState.Page -> current.copy(isLoading = true)
                is CharactersState.Error -> CharactersState.Page(
                    characters = emptyList(),
                    isLoading = true
                )
            }
        }

        Log.d("CharactersState","$searchQuery")

        try {
            var hasNextPage = true
            val charactersFromDb = charactersDao.getCharactersByPageWithFilter(
                name = searchQuery.name,
                status = searchQuery.status,
                species = searchQuery.species,
                type = searchQuery.type,
                gender = searchQuery.gender,
                page = nextPageDB,
            ).map { mapper.mapCharacterDBModelToCharacter(it) }

            val characters = if (charactersFromDb.isNotEmpty()) {
                charactersFromDb
            } else {
                val currentList =
                    (currentState as? CharactersState.Page)?.characters ?: emptyList()
                if (isConnectedToInternet(application)) {
                    val charactersFromApi = mutableListOf<CharacterDto>()
                    while (hasNextPage) {
                        val response = api.getCharacters(
                            page = nextPageNet,
                            name = searchQuery.name,
                            status = searchQuery.status,
                            species = searchQuery.species,
                            type = searchQuery.type,
                            gender = searchQuery.gender
                        )
                        hasNextPage = response.info.next != null
                        nextPageNet++

                        charactersFromApi += response.results.filter { characterDto -> currentList.find { it.id == characterDto.id } == null }


                        if (charactersFromApi.isNotEmpty()) break
                    }



                    charactersDao.insertCharacters(charactersFromApi.map {
                        mapper.mapCharacterDtoToCharacterDBModel(it)
                    })

                    charactersFromApi.map { mapper.mapCharacterDtoToCharacter(it) }
                } else {
                    if (currentList.isEmpty()){
                        throw RuntimeException("Try to check Internet connection")
                    }
                    emptyList()
                }
            }
            Log.d("Characters", "$characters")

            _charactersState.update { current ->
                when (current) {
                    is CharactersState.Page -> {

                        current.copy(
                            characters = current.characters + characters,
                            pageNumberDB = nextPageDB,
                            pageNumberNet = nextPageNet,
                            isLoading = false,
                            hasNextPage = characters.isNotEmpty(),
                            searchFilter = searchQuery
                        )
                    }

                    else -> CharactersState.Page(
                        characters = characters,
                        pageNumberDB = nextPageDB,
                        pageNumberNet = nextPageNet,
                        isLoading = false,
                        hasNextPage = characters.isNotEmpty(),
                        searchFilter = searchQuery
                    )
                }
            }

        } catch (e: HttpException){
            if (e.code() == 404){
                _charactersState.update { current ->
                    when (current){
                        is CharactersState.Page -> current.copy(hasNextPage = false, isLoading = false)
                        else -> CharactersState.Error(
                            message = "Failed to load characters: ${e.message}"
                        )
                    }
                }
            } else {
                throw e
            }

        } catch (e: Exception) {
            _charactersState.update { current ->
                CharactersState.Error(
                    message = "Failed to load characters: ${e.message}"
                )
            }
        }
    }

    override suspend fun refresh() {
        loadNextPage()
    }

    override fun changeSearchFilter(searchFilter: SearchFilter) {
        _charactersState.update { current ->
            when (current) {
                is CharactersState.Page -> current.copy(
                    searchFilter = searchFilter,
                    pageNumberDB = 0,
                    pageNumberNet = 0,
                    hasNextPage = true,
                    characters = emptyList()
                )

                else -> CharactersState.Page(searchFilter = searchFilter)
            }
        }
        Log.d("CharactersFeedViewModel", "${_charactersState.value}")
    }


    override suspend fun getCharacterById(id: Int): Character? {
        return try {
            charactersDao.getCharacterById(id)?.let{mapper.mapCharacterDBModelToCharacter(it)}
                ?: if(isConnectedToInternet(application)) {
                    val characterDto = api.getCharacterById(id)
                    val character = mapper.mapCharacterDtoToCharacter(characterDto)
                    charactersDao.insertCharacters(listOf(mapper.mapCharacterDtoToCharacterDBModel(characterDto)))

                    character
                } else {
                    null
                }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getFavoriteCharacters(): List<Character> {
        return charactersDao.getFavoriteCharacters().map { mapper.mapCharacterDBModelToCharacter(it) }
    }

    override suspend fun toggleFavorite(characterId: Int) {
        charactersDao.toggleFavorite(characterId)
        updateCharacterLikeStatus(characterId, !getCurrentLikeStatus(characterId))
    }

    fun updateCharacterLikeStatus(characterId: Int, isLiked: Boolean) {
        val currentState = _charactersState.value
        if (currentState is CharactersState.Page) {
            val updatedCharacters = currentState.characters.map { character ->
                if (character.id == characterId) {
                    character.copy(isLiked = isLiked)
                } else {
                    character
                }
            }
            _charactersState.value = currentState.copy(characters = updatedCharacters)
        }
    }

    private fun getCurrentLikeStatus(characterId: Int): Boolean {
        val currentState = _charactersState.value
        return if (currentState is CharactersState.Page) {
            currentState.characters.find { it.id == characterId }?.isLiked ?: false
        } else {
            false
        }
    }

}