package com.smart.movieslist.data.storage.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smart.movieslist.data.model.MovieModel

@Database(
    entities = [
        MovieModel::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppLocalDB : RoomDatabase() {
    abstract fun appDB(): AppDao


    companion object {
        @Volatile
        private var instance: AppLocalDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =Room.databaseBuilder(
            context,
            AppLocalDB::class.java,
            "moviesListDB"
        ).allowMainThreadQueries().build()

    }
}