package com.example.bestshowever.presentation.charaters_feed

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bestshowever.di.component.ApplicationComponent
import com.example.bestshowever.domain.entity.Character
import com.example.bestshowever.domain.entity.CharactersState
import com.example.bestshowever.domain.entity.SearchFilter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersFeedComponent(
    appComponent: ApplicationComponent,
    paddingValues: PaddingValues,
    onCharacterClickListener: (Int) -> Unit

) {
    val viewModelFactory = appComponent.getViewModelFactory()
    val viewModel: CharactersFeedViewModel = viewModel(factory = viewModelFactory)

    val screenState = viewModel.characterState.collectAsStateWithLifecycle().value

    val searchFilterState = remember {
        mutableStateOf(
            (screenState as? CharactersState.Page)?.searchFilter ?: SearchFilter()
        )
    }

    Log.d("CharactersState", screenState.toString())

    val refreshState = rememberPullToRefreshState()
    val isRefreshing by remember {
        mutableStateOf(false)
    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(4.dp), contentAlignment = Alignment.CenterEnd
        ) {
            SimpleSearchButton(
                searchFilter = searchFilterState.value,
                onSearch = { searchFilter -> viewModel.changeSearchFilter(searchFilter) }
            )
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.refresh()
            },
            modifier = Modifier.fillMaxSize(),
            state = refreshState,
        ) {
            when (screenState) {
                CharactersState.Initial -> {
                    LaunchedEffect(Unit) {
                        viewModel.loadNextPage()
                    }

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is CharactersState.Page -> {
                    LaunchedEffect(screenState.searchFilter) {
                        searchFilterState.value = screenState.searchFilter
                    }

                    if (screenState.characters.isEmpty() && !screenState.hasNextPage) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No such a characters")
                        }
                    }

                    CharactersGrid(
                        characters = screenState.characters,
                        isLoading = screenState.isLoading,
                        hasNextPage = screenState.hasNextPage,
                        paddingValues = paddingValues,
                        onLoadMore = {
                            viewModel.loadNextPage()
                        },
                        onCharacterClickListener = onCharacterClickListener,
                        onCharacterLikeClickListener = {
                            viewModel.changeLikeStatus(it)
                        }
                    )
                }

                is CharactersState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error ${screenState.message}")
                    }
                }
            }
        }
    }

}


@Composable
fun CharactersGrid(
    characters: List<Character>,
    isLoading: Boolean,
    hasNextPage: Boolean,
    onLoadMore: () -> Unit,
    paddingValues: PaddingValues,
    onCharacterClickListener: (Int) -> Unit,
    onCharacterLikeClickListener: (Int) -> Unit,
) {
    val lazyListState = rememberLazyGridState()


    val currentVisibleItemScrollOffset: Int by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex }
    }

    LaunchedEffect(currentVisibleItemScrollOffset) {
        if (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == characters.size - 1) {
            onLoadMore()
        }
    }

    LaunchedEffect(characters.size, hasNextPage, isLoading) {


        if (
            hasNextPage &&
            !isLoading &&
            characters.size in 1..6
        ) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = lazyListState,
        modifier = Modifier
            .padding(paddingValues)


    ) {

        items(characters) { character ->
            CharacterItem(character = character, onCharacterClickListener, onCharacterLikeClickListener )
        }
        if (isLoading && hasNextPage) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

        }
    }


}

@Composable
fun SearchDialog(
    searchFilter: SearchFilter,
    onSearch: (SearchFilter) -> Unit,
    onDismiss: () -> Unit
) {

    var searchText by remember { mutableStateOf(searchFilter.name) }
    var selectedStatus by remember { mutableStateOf(searchFilter.status) }
    var selectedSpecies by remember { mutableStateOf(searchFilter.species) }
    var selectedGender by remember { mutableStateOf(searchFilter.gender) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Characters") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search characters...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null)
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Status", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("", "Alive", "Dead", "unknown")) { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = {
                                selectedStatus = if (selectedStatus == status) "" else status
                            },
                            label = { Text(if (status.isEmpty()) "Any" else status) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Species", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        listOf(
                            "",
                            "Human",
                            "Alien",
                            "Robot",
                            "Animal",
                            "Mythological"
                        )
                    ) { species ->
                        FilterChip(
                            selected = selectedSpecies == species,
                            onClick = {
                                selectedSpecies = if (selectedSpecies == species) "" else species
                            },
                            label = { Text(if (species.isEmpty()) "Any" else species) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Gender", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("", "Male", "Female", "Genderless", "unknown")) { gender ->
                        FilterChip(
                            selected = selectedGender == gender,
                            onClick = {
                                selectedGender = if (selectedGender == gender) "" else gender
                            },
                            label = { Text(if (gender.isEmpty()) "Any" else gender) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSearch(
                        SearchFilter(
                            name = searchText,
                            status = selectedStatus,
                            species = selectedSpecies,
                            gender = selectedGender
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SimpleSearchButton(
    searchFilter: SearchFilter,
    onSearch: (SearchFilter) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(Icons.Default.List, "Search")
    }

    if (showDialog) {
        SearchDialog(
            searchFilter = searchFilter,
            onSearch = onSearch,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun CharacterItem(
    character: Character,
    onCharacterClickListener: (Int) -> Unit,
    onCharacterLikeClickListener: (Int) -> Unit,
) {
    var isFavorite by remember (character.id) { mutableStateOf(character.isLiked) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable {
                onCharacterClickListener(character.id)
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = "Image of ${character.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = character.status,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = character.species,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = character.gender,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        onCharacterLikeClickListener(character.id)
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = "",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}