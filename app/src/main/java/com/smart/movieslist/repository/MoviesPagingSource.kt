package com.smart.movieslist.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.smart.movieslist.data.model.MovieModel
import com.smart.movieslist.data.storage.local.db.AppDao
import com.smart.movieslist.data.storage.remote.ApiService
import com.smart.movieslist.repository.MoviesRepository.Companion.NETWORK_PAGE_SIZE
import com.smart.movieslist.repository.MoviesRepository.Companion.STARTING_PAGE_INDEX
import com.smart.movieslist.utils.DataResource
import com.smart.movieslist.utils.safeApiCall


class MoviesPagingSource(
    private val service: ApiService,
    private val db: AppDao,
    private val query: String
) : PagingSource<Int, MovieModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieModel> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val apiQuery = query

        val response = safeApiCall {
            if (apiQuery.isEmpty())
                service.getMovies(page)
            else
                service.searchMovies(apiQuery,page)
        }
        return when (response) {
            is DataResource.Success -> {

                val moviesList = synchronizeListWithFavorite(response.value.movieModels)
                val nextKey = if (moviesList.isEmpty()) {
                    null
                } else {
                    page + (params.loadSize / NETWORK_PAGE_SIZE)
                }
                LoadResult.Page(
                    data = moviesList,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = nextKey
                )
            }
            is DataResource.Error -> {
                LoadResult.Error(response.exception)
            }
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, MovieModel>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun synchronizeListWithFavorite(list: List<MovieModel>): List<MovieModel> {
        val favorite = db.getFavoriteList()
        favorite.map {
            val n = list.indexOf(it)
            if (n > -1)
                list[n].isFav = it.isFav
        }
        return list
    }
}
