package com.example.myandroidapp.main.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val _id: String = "",
    val title: String = "",
    val director: String = "",
    val description: String = "",
    val isFavourite: Boolean = false,
    val isPendingSync: Boolean = false
)
