package com.smart.movieslist.data.storage.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.smart.movieslist.data.model.MovieModel
import kotlinx.coroutines.flow.Flow


@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMoveToFavorite(movie: MovieModel):Long

    @Query("SELECT * FROM favoriteMovies")
    fun getFavoriteList(): List<MovieModel>

    @Query("SELECT EXISTS(SELECT * FROM favoriteMovies WHERE id = :id)")
    fun chickIfMoveInFavorite(id:Int) : LiveData<Boolean>

    @Query("DELETE FROM favoriteMovies WHERE id = :id")
    fun removeMoveFromFavorite(id:Int):Int

    @Query("DELETE FROM favoriteMovies")
    fun clearFavoriteItems()


}