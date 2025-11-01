package com.example.bestshowever.presentation.favorite_characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bestshowever.di.component.ApplicationComponent
import com.example.bestshowever.presentation.charaters_feed.CharacterItem

@Composable
fun FavoriteCharactersComponent(
    appComponent: ApplicationComponent,
    paddingValues: PaddingValues,
    onCharacterClickListener: (Int) -> Unit
) {
    val viewModelFactory = appComponent.getViewModelFactory()
    val viewModel: FavoriteCharactersViewModel = viewModel(factory = viewModelFactory)
    val characterState = viewModel.characterState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.loadCharacters()
    }


    when (characterState) {
        is FavoriteCharactersViewModel.CharactersState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is FavoriteCharactersViewModel.CharactersState.Success -> {
            val lazyListState = rememberLazyGridState()


            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = lazyListState,
                modifier = Modifier
                    .padding(paddingValues)


            ) {

                items(characterState.characters) { character ->
                    CharacterItem(character = character,
                        onCharacterClickListener,
                        onCharacterLikeClickListener ={ viewModel.toggleFavorite(it)}

                    )
                }
            }
        }

        is FavoriteCharactersViewModel.CharactersState.Error -> {
            val error = characterState.message
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Error",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = { viewModel.loadCharacters() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}