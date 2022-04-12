package com.smart.movieslist.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.smart.movieslist.data.model.MovieDetailsResponse
import com.smart.movieslist.data.model.MovieModel
import com.smart.movieslist.data.storage.local.db.AppDao
import com.smart.movieslist.data.storage.remote.ApiService
import com.smart.movieslist.utils.DataResource
import com.smart.movieslist.utils.safeApiCall
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that works with local and remote data sources.
 */
class MoviesRepository(private val service: ApiService,
                       private val db: AppDao
) {

    /**
     * Search repositories whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    fun getSearchResultStream(query: String): Flow<PagingData<MovieModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = { MoviesPagingSource(service,db, query) }
        ).flow
    }

    fun addMoveToFavorite(move:MovieModel){
        db.addMoveToFavorite(move)
    }

    fun removeItemFromFavorite(move:MovieModel){
        db.removeMoveFromFavorite(move.id)
    }

    suspend fun getMovieDetails(movieId:Int): DataResource<MovieDetailsResponse> {
       return safeApiCall{service.movieDetails(movieId)}
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
        const val NETWORK_PAGE_SIZE = 20
    }
}
