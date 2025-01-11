package com.example.myandroidapp.main.data.remote

import com.example.myandroidapp.main.data.Movie
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MovieService {
    @GET("/api/movie")
    suspend fun find(@Header("Authorization") authorization: String): List<Movie>

    @GET("/api/movie/{id}")
    suspend fun read(
        @Header("Authorization") authorization: String,
        @Path("id") movieId: String?
    ): Movie;

    @Headers("Content-Type: application/json")
    @POST("/api/movie")
    suspend fun create(@Header("Authorization") authorization: String, @Body movie: Movie): Movie

    @Headers("Content-Type: application/json")
    @PUT("/api/movie/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") movieId: String?,
        @Body movie: Movie
    ): Movie
}