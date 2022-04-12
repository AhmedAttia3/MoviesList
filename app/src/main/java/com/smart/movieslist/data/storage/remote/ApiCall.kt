package com.smart.movieslist.data.storage.remote

import android.util.Log
import com.google.gson.Gson
import com.smart.movieslist.data.model.MovieDetailsResponse
import com.smart.movieslist.data.model.MoviesResponse
import com.smart.movieslist.utils.Constants.API_KEY
import com.smart.movieslist.utils.Constants.BASE_URL
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL

class ApiCall : ApiService {
    override suspend fun getMovies(page: Int): MoviesResponse {
        val response = httpRequest(
            endPoint = "discover/movie",
            params = "&page=${page}"
        )

        return Gson().fromJson(response, MoviesResponse::class.java)
    }

    override suspend fun searchMovies(searchQuery: String, page: Int): MoviesResponse {
        val response = httpRequest(
            endPoint = "search/movie",
            params = "&query=${searchQuery}&language=en-US&page=${page}"
        )

        return Gson().fromJson(response, MoviesResponse::class.java)
    }

    override suspend fun movieDetails(movieId: Int): MovieDetailsResponse {
        val response = httpRequest(
            endPoint = "movie/$movieId"
        )

        return Gson().fromJson(response, MovieDetailsResponse::class.java)
    }

    private fun httpRequest(endPoint: String, params: String? = null): String {
            var stringResponse = ""
                var url = "${BASE_URL}${endPoint}?api_key=${API_KEY}"
                params?.let { url += it }
                val u = URL(url)
                Log.i("Start Request URL=>", url)
                with(u.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Content-length", "0")
                    useCaches = false
                    allowUserInteraction = false
                    connectTimeout = 5000
                    readTimeout = 5000
                    val reader = BufferedReader(InputStreamReader(inputStream) as Reader?)

                    val response = StringBuffer()
                    var inputLine = reader.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = reader.readLine()
                    }
                    reader.close()
                    Log.i("End Request URL=> ", url)
//                    Log.i("Response ==>", response.toString())
                    stringResponse = response.toString()
                }
            return stringResponse
    }
}