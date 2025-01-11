package com.example.myandroidapp.main.ui.movies

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myandroidapp.main.data.Movie
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign

typealias OnMovieFn = (id: String?) -> Unit

@Composable
fun MovieList(movieList: List<Movie>, onMovieClick: OnMovieFn, modifier: Modifier) {
    Log.d("MovieList", "recompose")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items (movieList) { movie ->
            MovieDetail(movie, onMovieClick)
        }
    }
}

@Composable
fun MovieDetail(movie: Movie, onMovieClick: OnMovieFn) {
    Log.d("MovieDetail", "recompose id = ${movie._id}")
    var isExpanded by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

//    Row {
//        ClickableText(text = AnnotatedString(movie.title),
//            style = TextStyle(
//                fontSize = 24.sp,
//            ), onClick = { onMovieClick(movie._id) })
//    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 300) { // Detect double-click
                    onMovieClick(movie._id)
                } else {
                    isExpanded = !isExpanded // Toggle expansion on single click
                }
                lastClickTime = currentTime
            }
            .animateContentSize()
//            .padding(8.dp)
    ) {
        Text(
            text = movie.title,
            style = TextStyle(fontSize = 24.sp)
        )
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Director: ${movie.director}", textAlign = TextAlign.Left)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Description: ${movie.description}", textAlign = TextAlign.Left)
        }
    }
}
