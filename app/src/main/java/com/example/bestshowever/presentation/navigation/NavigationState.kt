package com.example.bestshowever.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun navigateTo(route:String){
        navHostController.navigate(route){

            popUpTo(navHostController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }


    fun navigateToCharacterDetailed(id: Int){
        navHostController.navigate(Screen.CharacterDetailed.getRouteWithArgs(id))
    }
}

@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
): NavigationState{
    return remember {
        NavigationState(navHostController)
    }
}