package com.smart.movieslist.utils



sealed class DataResource<out T> {
    data class Success<out T>(val value: T): DataResource<T>()
    data class Error<out T>(val exception: Throwable) : DataResource<T>()
}

