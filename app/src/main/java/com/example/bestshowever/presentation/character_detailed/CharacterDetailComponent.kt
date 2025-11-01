package com.example.bestshowever.presentation.character_detailed

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bestshowever.di.component.ApplicationComponent
import com.example.bestshowever.domain.entity.Character
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailComponent(
    appComponent: ApplicationComponent,
    characterId: Int,
    paddingValues: PaddingValues,
    onBackClick: () -> Unit
) {
    val viewModelFactory = appComponent.getViewModelFactory()
    val viewModel: CharacterDetailViewModel = viewModel(factory = viewModelFactory)
    val characterState by viewModel.characterState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            TopAppBar(
                title = { Text("Character Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingVals ->
        when (characterState) {
            is CharacterDetailViewModel.CharacterDetailState.Loading -> {
                LaunchedEffect(Unit) {
                    viewModel.loadCharacter(characterId)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingVals),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CharacterDetailViewModel.CharacterDetailState.Success -> {
                val character = (characterState as CharacterDetailViewModel.CharacterDetailState.Success).character
                CharacterDetailContent(
                    modifier = Modifier.padding(paddingVals),
                    character = character,
                )
            }

            is CharacterDetailViewModel.CharacterDetailState.Error -> {
                val error = (characterState as CharacterDetailViewModel.CharacterDetailState.Error).message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingVals),
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
                        Button(onClick = { viewModel.loadCharacter(characterId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterDetailContent(
    character: Character,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            AsyncImage(modifier = Modifier.fillMaxSize(),
                model = character.image,
                contentDescription = "Image of ${character.name}",
                contentScale = ContentScale.Crop,
            )
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                text = character.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoRow(
                title = "Status",
                value = character.status,
                showStatusIndicator = true,
                status = character.status
            )
            InfoRow(
                title = "Species",
                value = character.species
            )
            if (character.type.isNotEmpty()) {
                InfoRow(
                    title = "Type",
                    value = character.type
                )
            }
            InfoRow(
                title = "Gender",
                value = character.gender
            )
            InfoRow(
                title = "Origin",
                value = character.origin
            )
            InfoRow(
                title = "Location",
                value = character.location
            )
            InfoRow(
                title = "Episodes",
                value = "${character.episodeUrls.size} episodes"
            )
            InfoRow(
                title = "Created",
                value = formatDate(character.created)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun InfoRow(
    title: String,
    value: String,
    showStatusIndicator: Boolean = false,
    status: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showStatusIndicator) {
                    StatusIndicator(status = status)
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun StatusIndicator(status: String) {
    val color = when (status.lowercase()) {
        "alive" -> Color.Green
        "dead" -> Color.Red
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color, CircleShape)
    )
}


private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy.mm.dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}