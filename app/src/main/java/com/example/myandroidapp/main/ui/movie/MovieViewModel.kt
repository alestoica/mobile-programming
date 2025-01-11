package com.example.myandroidapp.main.ui.movie

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myandroidapp.MyApplication
import com.example.myandroidapp.core.Result
import com.example.myandroidapp.core.TAG
import com.example.myandroidapp.main.data.Movie
import com.example.myandroidapp.main.data.MovieRepository
import kotlinx.coroutines.launch
import java.util.UUID

data class MovieUiState(
    val movieId: String? = null,
    val movie: Movie = Movie(),
    var loadResult: Result<Movie>? = null,
    var submitResult: Result<Movie>? = null,
)

class MovieViewModel(private val movieId: String?, private val movieRepository: MovieRepository) :
    ViewModel() {

    var uiState: MovieUiState by mutableStateOf(MovieUiState(loadResult = Result.Loading))
        private set

    init {
        Log.d(TAG, "init")
        if (movieId != null) {
            loadMovie()
        } else {
            uiState = uiState.copy(loadResult = Result.Success(Movie()))
        }
    }

    private fun loadMovie() {
        viewModelScope.launch {
            movieRepository.movieStream.collect { movies ->
                if (uiState.loadResult !is Result.Loading) {
                    return@collect
                }
                val movie = movies.find { it._id == movieId } ?: Movie()
                Log.d(TAG, "Loaded movie: $movie")
                uiState = uiState.copy(movie = movie, loadResult = Result.Success(movie))
            }
        }
    }

    fun saveOrUpdateMovie(
        title: String,
        director: String,
        description: String,
        isFavourite: Boolean
    ) {
        viewModelScope.launch {
            Log.d(TAG, "saveOrUpdateMovie...");
            try {
                uiState = uiState.copy(submitResult = Result.Loading)
                val movie = uiState.movie.copy(
                    _id = uiState.movie._id.ifEmpty { movieId ?: UUID.randomUUID().toString() },
                    title = title.ifBlank { uiState.movie.title },
                    director = director.ifBlank { uiState.movie.director },
                    description = description.ifBlank { uiState.movie.description },
                    isFavourite = isFavourite,
                    isPendingSync = false
                )
                if (movieId == null) {
                    movieRepository.save(movie)
                    movieRepository.showNotification(
                        "Movie Added",
                        "The movie '${movie.title}' was added."
                    )
                } else {
                    movieRepository.update(movie)
                    movieRepository.showNotification(
                        "Movie Updated",
                        "The movie '${movie.title}' was updated."
                    )
                }
                Log.d(TAG, "saveOrUpdateMovie succeeded");
                uiState = uiState.copy(submitResult = Result.Success(movie))
            } catch (e: Exception) {
                Log.d(TAG, "saveOrUpdateMovie failed");
                uiState = uiState.copy(submitResult = Result.Error(e))
            }
        }
    }

    fun saveOrUpdateMovieOffline(
        title: String,
        director: String,
        description: String,
        isFavourite: Boolean
    ) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(submitResult = Result.Loading)
                val movie = uiState.movie.copy(
                    _id = uiState.movie._id.ifEmpty { movieId ?: UUID.randomUUID().toString() },
                    title = title,
                    director = director,
                    description = description,
                    isFavourite = isFavourite,
                    isPendingSync = true
                )
                if (movieId == null) {
                    movieRepository.handleMovieCreated(movie)
                    movieRepository.showNotification(
                        "Movie Saved Locally",
                        "Changes to '${movie.title}' will be synced when online."
                    )
                } else {
                    movieRepository.handleMovieUpdated(movie)
                    movieRepository.showNotification(
                        "Movie Updated Locally",
                        "Changes to '${movie.title}' will be synced when online."
                    )
                }
                uiState = uiState.copy(submitResult = Result.Success(movie))
            } catch (e: Exception) {
                Log.d(TAG, "saveOrUpdateMovie failed");
                uiState = uiState.copy(submitResult = Result.Error(e))
            }
        }
    }


    companion object {
        fun Factory(movieId: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                MovieViewModel(movieId, app.container.movieRepository)
            }
        }
    }
}
