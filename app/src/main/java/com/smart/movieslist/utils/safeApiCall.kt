package com.smart.movieslist.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(call: suspend () -> T): DataResource<T> {
    return withContext(Dispatchers.IO) {
        try {
            DataResource.Success(call.invoke())
        } catch (throwable: Throwable) {
            Log.e("safeCall error message", throwable.toString())
            DataResource.Error(throwable)
        }
    }
}

