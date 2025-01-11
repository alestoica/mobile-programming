package com.example.myandroidapp.services.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myandroidapp.MyApplication
import com.example.myandroidapp.main.data.MovieRepository

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository: MovieRepository by lazy {
        (applicationContext as MyApplication).container.movieRepository
    }

    override suspend fun doWork(): Result {
        val pendingMovies = repository.getPendingSyncMovies()

        return try {
            for (movie in pendingMovies) {
                if (movie._id.isNotEmpty()) {
                    repository.update(movie.copy(isPendingSync = false))
                } else {
                    repository.save(movie.copy(isPendingSync = false))
                }
                repository.markAsSynced(movie._id)
            }

            repository.showNotification("Sync Completed", "Your changes have been synced successfully.")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}