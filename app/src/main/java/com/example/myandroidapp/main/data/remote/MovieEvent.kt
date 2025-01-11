package com.example.myandroidapp.main.data.remote

import com.example.myandroidapp.main.data.Movie

data class MovieEvent(val type: String, val payload: Movie)