package com.example.myandroidapp.main.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myandroidapp.main.data.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM Movies")
    fun getAll(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movies: List<Movie>)

    @Update
    suspend fun update(movie: Movie): Int

    @Query("DELETE FROM Movies WHERE _id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM Movies")
    suspend fun deleteAll()

    @Query("SELECT * FROM Movies WHERE isPendingSync = 1")
    suspend fun getPendingSyncMovies(): List<Movie>

    @Query("UPDATE Movies SET isPendingSync = 0 WHERE _id = :id")
    suspend fun markAsSynced(id: String)
}
