package com.example.bestshowever.domain.entity

data class Character(
    val id: Int,
    val name: String,
    val species: String,
    val status: String,
    val gender: String,
    val type: String,
    val image: String,
    val origin: String,
    val location: String,
    val episodeUrls: List<String>,
    val created: String,
    val isLiked: Boolean = false
)
