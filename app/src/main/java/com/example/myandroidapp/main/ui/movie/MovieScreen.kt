package com.example.myandroidapp.main.ui.movie

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myandroidapp.core.Result
import com.example.myandroidapp.R
import com.example.myandroidapp.services.ui.MyNetworkStatusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(movieId: String?, onClose: () -> Unit) {
    val movieViewModel = viewModel<MovieViewModel>(factory = MovieViewModel.Factory(movieId))
    val movieUiState = movieViewModel.uiState
    val networkStatusViewModel = viewModel<MyNetworkStatusViewModel>(factory = MyNetworkStatusViewModel.Factory(LocalContext.current.applicationContext as Application))
    val networkStatusUiState = networkStatusViewModel.uiState
    var title by rememberSaveable { mutableStateOf(movieUiState.movie.title) }
    var director by rememberSaveable { mutableStateOf(movieUiState.movie.director) }
    var description by rememberSaveable { mutableStateOf(movieUiState.movie.description) }
    var isFavourite by rememberSaveable { mutableStateOf(movieUiState.movie.isFavourite) }

    Log.d("MovieScreen", "recompose, title = $title")

    LaunchedEffect(movieUiState.submitResult) {
        Log.d("MovieScreen", "Submit = ${movieUiState.submitResult}");
        if (movieUiState.submitResult is Result.Success) {
            Log.d("MovieScreen", "Closing screen");
            onClose();
        }
    }

    var titleInitialized by remember { mutableStateOf(movieId == null) }
    LaunchedEffect(movieId, movieUiState.loadResult) {
        Log.d("MovieScreen", "Text initialized = ${movieUiState.loadResult}");

        if (titleInitialized) {
            return@LaunchedEffect
        }

        if (movieUiState.loadResult is Result.Success) {
            val loadedMovie = movieUiState.movie
            title = loadedMovie.title
            director = loadedMovie.director
            description = loadedMovie.description
            isFavourite = loadedMovie.isFavourite
            titleInitialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.movie)) },
                actions = {
                    Button(onClick = {
                        Log.d("MovieScreen", "save movie title = $title");
                        if (networkStatusUiState)
                            movieViewModel.saveOrUpdateMovie(title, director, description, isFavourite)
                        else
                            movieViewModel.saveOrUpdateMovieOffline(title, director, description, isFavourite)
                    }) { Text("Save") }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (movieUiState.loadResult is Result.Loading) {
                CircularProgressIndicator()
                return@Scaffold
            }

            if (movieUiState.submitResult is Result.Loading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { LinearProgressIndicator() }
            }

            if (movieUiState.loadResult is Result.Error) {
                Text(text = "Failed to load movie - ${(movieUiState.loadResult as Result.Error).exception?.message}")
            }

            Row {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row {
                TextField(
                    value = director,
                    onValueChange = { director = it },
                    label = { Text("Director") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row {
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Favorite", modifier = Modifier.padding(start = 8.dp))
                Checkbox(
                    checked = isFavourite,
                    onCheckedChange = { isFavourite = it }
                )
            }


            if (movieUiState.submitResult is Result.Error) {
                Text(
                    text = "Failed to submit movie - ${(movieUiState.submitResult as Result.Error).exception?.message}",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMovieScreen() {
    MovieScreen(movieId = "0", onClose = {})
}
