package com.smart.movieslist.data.model

import com.google.gson.annotations.SerializedName

data class MoviesResponse(
    val page: Int,
    @SerializedName("results")
    val movieModels: List<MovieModel>,
    val total_pages: Int,
    val total_results: Int
)