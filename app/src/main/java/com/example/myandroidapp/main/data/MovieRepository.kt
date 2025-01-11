package com.example.myandroidapp.main.data

import android.util.Log
import com.example.myandroidapp.MyApplication
import com.example.myandroidapp.core.TAG
import com.example.myandroidapp.core.data.remote.Api
import com.example.myandroidapp.main.data.local.MovieDao
import com.example.myandroidapp.main.data.remote.MovieEvent
import com.example.myandroidapp.main.data.remote.MovieService
import com.example.myandroidapp.main.data.remote.MovieWsClient
import com.example.myandroidapp.services.utils.showSimpleNotificationWithTapAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class MovieRepository(
    private val movieService: MovieService,
    private val movieWsClient: MovieWsClient,
    private val movieDao: MovieDao
) {
    val movieStream by lazy { movieDao.getAll() }

    init {
        Log.d(TAG, "init")
    }

    private fun getBearerToken() = "Bearer ${Api.tokenInterceptor.token}"

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            val movies = movieService.find(authorization = getBearerToken())
            movieDao.deleteAll()
            movies.forEach { movieDao.insert(it) }
            Log.d(TAG, "refresh succeeded")
        } catch (e: Exception) {
            Log.w(TAG, "refresh failed", e)
        }
    }

    suspend fun openWsClient() {
        Log.d(TAG, "openWsClient")
        withContext(Dispatchers.IO) {
            getMovieEvents().collect {
                Log.d(TAG, "Movie event collected $it")
                if (it.isSuccess) {
                    val movieEvent = it.getOrNull();
                    when (movieEvent?.type) {
                        "created" -> handleMovieCreated(movieEvent.payload)
                        "updated" -> handleMovieUpdated(movieEvent.payload)
                        "deleted" -> handleMovieDeleted(movieEvent.payload)
                    }
                }
            }
        }
    }

    suspend fun closeWsClient() {
        Log.d(TAG, "closeWsClient")
        withContext(Dispatchers.IO) {
            movieWsClient.closeSocket()
        }
    }

    private fun getMovieEvents(): Flow<Result<MovieEvent>> = callbackFlow {
        Log.d(TAG, "getMovieEvents started")
        movieWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if (it != null) {
                    trySend(Result.success(it))
                }
            },
            onClosed = { close() },
            onFailure = { close() });
        awaitClose { movieWsClient.closeSocket() }
    }

    fun showNotification(title: String, content: String) {
        val context = MyApplication.appContext
        val channelId = "My Channel"
        showSimpleNotificationWithTapAction(context, channelId, title, content)
    }

    suspend fun update(movie: Movie): Movie {
        Log.d(TAG, "update $movie...")
        val updatedMovie =
            movieService.update(movieId = movie._id, movie = movie, authorization = getBearerToken())
        Log.d(TAG, "update $movie succeeded")
        handleMovieUpdated(updatedMovie)
        return updatedMovie
    }

    suspend fun save(movie: Movie): Movie {
        Log.d(TAG, "save $movie...")
        val createdMovie = movieService.create(movie = movie, authorization = getBearerToken())
        Log.d(TAG, "save $movie succeeded")
        handleMovieCreated(createdMovie)
        return createdMovie
    }

//    suspend fun syncPendingChanges() {
//        val pendingMovies = movieDao.getPendingSyncMovies()
//        pendingMovies.forEach { movie ->
//            if (movie.isPendingSync) {
//                try {
//                    if (movie._id.isBlank()) {
//                        save(movie.copy(isPendingSync = false))
//                    } else {
//                        update(movie.copy(isPendingSync = false))
//                    }
//                    markAsSynced(movie._id)
//                } catch (e: Exception) {
//                    Log.w(TAG, "Failed to sync movie ${movie.title}", e)
//                }
//            }
//        }
//    }

    suspend fun getPendingSyncMovies(): List<Movie> {
        return movieDao.getPendingSyncMovies()
    }

    suspend fun markAsSynced(id: String) {
        return movieDao.markAsSynced(id)
    }

    private fun handleMovieDeleted(movie: Movie) {
        Log.d(TAG, "handleMovieDeleted - $movie")
    }

    suspend fun handleMovieUpdated(movie: Movie) {
        Log.d(TAG, "handleMovieUpdated...")
        movieDao.update(movie)
    }

    suspend fun handleMovieCreated(movie: Movie) {
        Log.d(TAG, "handleMovieCreated...")
        movieDao.insert(movie)
    }

    suspend fun deleteAll() {
        movieDao.deleteAll()
    }

    fun setToken(token: String) {
        movieWsClient.authorize(token)
    }
}