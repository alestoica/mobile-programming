package com.example.myandroidapp.main.ui.movies

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myandroidapp.MyApplication
import com.example.myandroidapp.core.TAG
import com.example.myandroidapp.main.data.Movie
import com.example.myandroidapp.main.data.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MoviesViewModel(private val movieRepository: MovieRepository) : ViewModel() {
    val uiState: Flow<List<Movie>> = movieRepository.movieStream

    init {
        Log.d(TAG, "init")
        loadMovies()
    }

    fun loadMovies() {
        Log.d(TAG, "loadMovies...")
        viewModelScope.launch {
            movieRepository.refresh()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                MoviesViewModel(app.container.movieRepository)
            }
        }
    }
}
