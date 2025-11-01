package com.example.bestshowever.presentation.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bestshowever.di.component.ApplicationComponent
import com.example.bestshowever.presentation.character_detailed.CharacterDetailComponent
import com.example.bestshowever.presentation.charaters_feed.CharactersFeedComponent
import com.example.bestshowever.presentation.favorite_characters.FavoriteCharactersComponent
import com.example.bestshowever.presentation.navigation.AppNavGraph
import com.example.bestshowever.presentation.navigation.NavigationItem
import com.example.bestshowever.presentation.navigation.rememberNavigationState

@Composable
fun MainPage(appComponent: ApplicationComponent){
    val navigationState = rememberNavigationState()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
                val items = listOf(
                    NavigationItem.Home,
                    NavigationItem.FavoriteNews,
                )

                items.forEach { item ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                        it.route == item.screen.route
                    } ?: false
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navigationState.navigateTo(item.screen.route)
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = "") },
                        label = { Text(text = stringResource(item.titleResId)) },
                    )
                }
            }
        },

    ) { paddingValues ->
        AppNavGraph(
            navigationState.navHostController,
            charactersFeedScreenContent = {
                CharactersFeedComponent(appComponent, paddingValues, onCharacterClickListener = { characterId ->
                    navigationState.navigateToCharacterDetailed(characterId)
                })
            },
            favoriteFeedScreenContent = {
                FavoriteCharactersComponent(
                    appComponent = appComponent,
                    paddingValues = paddingValues,
                    onCharacterClickListener = { characterId ->
                        navigationState.navigateToCharacterDetailed(characterId)
                    }
                )
            },
            characterDetailsContent = { characterId ->
                CharacterDetailComponent(
                    appComponent = appComponent,
                    characterId = characterId,
                    paddingValues = paddingValues
                ) { navigationState.navHostController.popBackStack() }
            },
        )
    }
}