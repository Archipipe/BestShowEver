package com.example.bestshowever.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class
CharacterDBModel(
    @PrimaryKey val id: Int,
    val name: String,
    val species: String,
    val status: String,
    val gender: String,
    val image: String,
    val type: String,
    val origin: String,
    val location: String,
    val episodeUrls: List<String>,
    val created: String,
    val isLiked: Boolean = false
)
