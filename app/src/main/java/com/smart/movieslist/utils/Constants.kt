package com.smart.movieslist.utils


object Constants {


    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = "282e4537daea6171a41e72d735dca93f"



enum class ErrorType {
    NETWORK,
    TIMEOUT,
    UNKNOWN
}

}