package com.smart.movieslist.data.model

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoriteMovies")
data class MovieModel(
    @PrimaryKey
    @Nullable
    val id: Int,
    val adult: Boolean,
    val backdrop_path: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int,
    var isFav: Boolean = false
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${poster_path}"
    val backdropUrl: String
        get() = "https://image.tmdb.org/t/p/w500${backdrop_path}"



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieModel

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}