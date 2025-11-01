package com.example.bestshowever.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun saveList(list: List<String>) = list.joinToString(",")

    @TypeConverter
    fun restoreList(data: String) = if (data.isBlank()) emptyList() else data.split(",")
}