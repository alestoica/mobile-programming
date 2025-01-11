package com.example.myandroidapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myandroidapp.auth.LoginScreen
import com.example.myandroidapp.core.data.UserPreferences
import com.example.myandroidapp.core.data.remote.Api
import com.example.myandroidapp.core.ui.UserPreferencesViewModel
import com.example.myandroidapp.main.ui.movie.MovieScreen
import com.example.myandroidapp.main.ui.movies.MoviesScreen

val moviesRoute = "movies"
val authRoute = "auth"

@Composable
fun MyAppNavHost() {
    val navController = rememberNavController()
    val onCloseMovie = {
        Log.d("MyAppNavHost", "navigate back to list")
        navController.popBackStack()
    }
    val userPreferencesViewModel =
        viewModel<UserPreferencesViewModel>(factory = UserPreferencesViewModel.Factory)
    val userPreferencesUiState by userPreferencesViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = UserPreferences()
    )
    val myAppViewModel = viewModel<MyAppViewModel>(factory = MyAppViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = authRoute
    ) {
        composable(moviesRoute) {
            MoviesScreen(
                onMovieClick = { movieId ->
                    Log.d("MyAppNavHost", "navigate to movie $movieId")
                    navController.navigate("$moviesRoute/$movieId")
                },
                onAddMovie = {
                    Log.d("MyAppNavHost", "navigate to new movie")
                    navController.navigate("$moviesRoute-new")
                },
                onLogout = {
                    Log.d("MyAppNavHost", "logout")
                    myAppViewModel.logout()
                    Api.tokenInterceptor.token = null
                    navController.navigate(authRoute) {
                        popUpTo(0)
                    }
                })
        }
        composable(
            route = "$moviesRoute/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        )
        {
            MovieScreen(
                movieId = it.arguments?.getString("id"),
                onClose = { onCloseMovie() }
            )
        }
        composable(route = "$moviesRoute-new")
        {
            MovieScreen(
                movieId = null,
                onClose = { onCloseMovie() }
            )
        }
        composable(route = authRoute)
        {
            LoginScreen(
                onClose = {
                    Log.d("MyAppNavHost", "navigate to list")
                    navController.navigate(moviesRoute)
                }
            )
        }
    }

    LaunchedEffect(userPreferencesUiState.token) {
        if (userPreferencesUiState.token.isNotEmpty()) {
            Log.d("MyAppNavHost", "Launched effect navigate to movies")
            Api.tokenInterceptor.token = userPreferencesUiState.token
            myAppViewModel.setToken(userPreferencesUiState.token)
            navController.navigate(moviesRoute) {
                popUpTo(0)
            }
        }
    }
}
