package com.smart.movieslist.utils

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.smart.movieslist.repository.MoviesRepository
import com.smart.movieslist.ui.home.HomeViewModel
import com.smart.movieslist.ui.movieDetails.MovieDetailsViewModel

/**
 * Factory for ViewModels
 */
class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: MoviesRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, handle) as T
        }
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
