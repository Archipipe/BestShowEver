package com.example.bestshowever.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    charactersFeedScreenContent: @Composable ()->Unit,
    characterDetailsContent: @Composable (characterId: Int)->Unit,
    favoriteFeedScreenContent: @Composable ()->Unit,
){
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route
    ){


        navigation(
            startDestination = Screen.CharactersFeed.route,
            route = Screen.Home.route
        ) {
            composable(Screen.CharactersFeed.route){
                charactersFeedScreenContent()
            }

            composable(
                route = Screen.CharacterDetailed.route,
                arguments = listOf(
                    navArgument(
                        Screen.CharacterDetailed.KEY_ID,
                        builder = {type = NavType.IntType}
                    ) )
            ) {
                val characterId = it.arguments?.getInt(Screen.CharacterDetailed.KEY_ID) ?: -1
                characterDetailsContent(characterId)
            }

        }

        composable(Screen.FavoriteNews.route){
            favoriteFeedScreenContent()
        }
    }
}