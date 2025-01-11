package com.example.myandroidapp

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.example.myandroidapp.auth.data.AuthRepository
import com.example.myandroidapp.auth.data.remote.AuthDataSource
import com.example.myandroidapp.core.TAG
import com.example.myandroidapp.core.data.UserPreferencesRepository
import com.example.myandroidapp.core.data.remote.Api
import com.example.myandroidapp.main.data.MovieRepository
import com.example.myandroidapp.main.data.remote.MovieService
import com.example.myandroidapp.main.data.remote.MovieWsClient

val Context.userPreferencesDataStore by preferencesDataStore(
    name = "user_preferences"
)

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    private val movieService: MovieService = Api.retrofit.create(MovieService::class.java)
    private val movieWsClient: MovieWsClient = MovieWsClient(Api.okHttpClient)
    private val authDataSource: AuthDataSource = AuthDataSource()

    private val database: MyAppDatabase by lazy { MyAppDatabase.getDatabase(context) }

    val movieRepository: MovieRepository by lazy {
        MovieRepository(movieService, movieWsClient, database.movieDao())
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authDataSource)
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.userPreferencesDataStore)
    }
}
