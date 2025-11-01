package com.example.bestshowever.presentation.navigation


sealed class Screen(val route: String){
    object Home: Screen(ROUTE_HOME)
    object CharactersFeed: Screen(ROUTE_CHARACTERS_FEED)
    object CharacterDetailed: Screen(ROUTE_CHARACTER_DETAILED){
        const val KEY_ID = CHARACTER_ID
        private const val ROOT_FOR_ARGS = "character_detailed"

        fun getRouteWithArgs(characterId: Int): String{

            return "$ROOT_FOR_ARGS/${characterId}"
        }
    }
    object FavoriteNews: Screen(ROUTE_FAVORITE_NEWS)

    private companion object{
        const val CHARACTER_ID = "character_id"

        const val ROUTE_HOME = "home"
        const val ROUTE_CHARACTERS_FEED = "characters_feed"
        const val ROUTE_CHARACTER_DETAILED = "character_detailed/{$CHARACTER_ID}"
        const val ROUTE_FAVORITE_NEWS = "favorite_news"

    }
}