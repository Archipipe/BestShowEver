package com.example.bestshowever.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.bestshowever.R

sealed class NavigationItem(
    val titleResId: Int,
    val icon: ImageVector,
    val screen: Screen
) {

    object Home : NavigationItem(
        titleResId = R.string.navigation_item_characters_feed,
        icon = Icons.Filled.Home,
        screen = Screen.Home
    )

    object FavoriteNews : NavigationItem(
        titleResId = R.string.navigation_item_favorite_characters,
        icon = Icons.Filled.Favorite,
        screen = Screen.FavoriteNews
    )
}