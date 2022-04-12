package com.smart.movieslist.data.storage.remote

import com.smart.movieslist.data.model.MovieDetailsResponse
import com.smart.movieslist.data.model.MoviesResponse


interface ApiService {

    suspend fun getMovies(page: Int): MoviesResponse

    suspend fun searchMovies(searchQuery: String, page: Int): MoviesResponse

    suspend fun movieDetails(movieId: Int): MovieDetailsResponse


}