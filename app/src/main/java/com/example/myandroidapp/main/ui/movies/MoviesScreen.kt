package com.example.myandroidapp.main.ui.movies

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myandroidapp.R
import com.example.myandroidapp.services.ui.MyJobs
import com.example.myandroidapp.services.ui.MyLocation
import com.example.myandroidapp.services.ui.MyNetworkStatus
import com.example.myandroidapp.services.ui.ProximitySensor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    onMovieClick: (id: String?) -> Unit,
    onAddMovie: () -> Unit,
    onLogout: () -> Unit
) {
    Log.d("MoviesScreen", "recompose")
    val moviesViewModel = viewModel<MoviesViewModel>(factory = MoviesViewModel.Factory)
    val moviesUiState by moviesViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = listOf()
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.movies)) },
                actions = {
                    MyNetworkStatus()
                    Button(onClick = onLogout) { Text("Logout") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("MoviesScreen", "add")
                    onAddMovie()
                },
            ) { Icon(Icons.Rounded.Add, "Add") }
        }
    )
    { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Movie list
            MovieList(
                movieList = moviesUiState,
                onMovieClick = onMovieClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            )

            // Spacer for separation
//            Spacer(modifier = Modifier.height(16.dp))

            // Proximity sensor
            ProximitySensor(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            // Spacer for separation
//            Spacer(modifier = Modifier.height(16.dp))

            // Location
            MyLocation(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .padding(bottom = 72.dp)
                    .weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun PreviewMoviesScreen() {
    MoviesScreen(onMovieClick = {}, onAddMovie = {}, onLogout = {})
}
