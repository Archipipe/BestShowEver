package com.example.bestshowever.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharactersDao {
    @Query("""
        SELECT * FROM characters 
        WHERE  (:name = '' OR name LIKE '%' || :name || '%')
        AND (:status = '' OR status = :status)
        AND (:species = '' OR species = :species)
        AND (:type = '' OR type = :type)
        AND (:gender = '' OR gender = :gender)
        ORDER BY id
        LIMIT 20 OFFSET (:page - 1) * 20
        
    """)
    suspend fun getCharactersByPageWithFilter(
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String,
        page: Int
    ): List<CharacterDBModel>


    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterDBModel?

    @Query("SELECT * FROM characters WHERE isLiked = true")
    suspend fun getFavoriteCharacters(): List<CharacterDBModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacters(characters: List<CharacterDBModel>)

    @Query("UPDATE characters SET isLiked = NOT isLiked WHERE id = :characterId")
    suspend fun toggleFavorite(characterId: Int)
}