package com.smart.movieslist.ui.movieDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.movieslist.repository.MoviesRepository
import com.smart.movieslist.utils.DataResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val repository: MoviesRepository
) : ViewModel() {
    private val _state:MutableStateFlow<UiStates> = MutableStateFlow(UiStates.Loading)
    val state :StateFlow<UiStates> = _state


    fun getMovieDetails(movieId:Int){
        viewModelScope.launch {
            when(val response = repository.getMovieDetails(movieId)){
                is DataResource.Error -> _state.value = UiStates.Error(response.exception)
                is DataResource.Success -> {
                    _state.value = UiStates.Success(response.value)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}

sealed class UiStates{
    object Loading : UiStates()
    data class Success<out T : Any>(val data: T?) : UiStates()
    data class Error(val exception: Throwable) : UiStates()
}