package com.example.bestshowever.domain.entity

sealed class CharactersState{
    object Initial: CharactersState()
    data class Page(
        val characters: List<Character> = emptyList(),
        val pageNumberDB: Int = 0,
        val pageNumberNet: Int = 0,
        val isLoading: Boolean = false,
        val hasNextPage: Boolean = true,
        val searchFilter: SearchFilter = SearchFilter()
    ) : CharactersState()
    data class Error (val message: String): CharactersState()
}


data class SearchFilter(
    val name: String = "",
    val status: String = "",
    val species: String = "",
    val type: String = "",
    val gender: String = "",
    ) {
    fun isEmpty(): Boolean {
        return name.isEmpty() && status.isEmpty() && species.isEmpty() &&
                type.isEmpty() && gender.isEmpty()
    }

    fun hasActiveFilters(): Boolean {
        return status.isNotEmpty() || species.isNotEmpty() ||
                type.isNotEmpty() || gender.isNotEmpty()
    }

    fun getActiveFilters(): List<Pair<String, String>> {
        val filters = mutableListOf<Pair<String, String>>()
        if (status.isNotEmpty()) filters.add("Status" to status)
        if (species.isNotEmpty()) filters.add("Species" to species)
        if (type.isNotEmpty()) filters.add("Type" to type)
        if (gender.isNotEmpty()) filters.add("Gender" to gender)
        return filters
    }
}
