package com.smart.movieslist.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.smart.movieslist.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

fun Context.showLongToast(message:String){
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_LONG
    ).show()
}

fun Throwable.getErrorType():Constants.ErrorType{
    return when (this) {
        is SocketTimeoutException -> Constants.ErrorType.TIMEOUT
        is IOException -> Constants.ErrorType.NETWORK
        else -> Constants.ErrorType.UNKNOWN
    }
}

fun Constants.ErrorType.getMessage(context: Context):String {
    return return when (this) {
        Constants.ErrorType.NETWORK -> context.resources.getString(R.string.noInternet)
        Constants.ErrorType.TIMEOUT ->  context.resources.getString(R.string.timeout)
        Constants.ErrorType.UNKNOWN ->  context.resources.getString(R.string.unknown)
    }
}

@SuppressLint("CheckResult")
@BindingAdapter(
    "srcUrl",
    "placeholder",
    requireAll = true // make the attributes required
)
fun ImageView.bindSrcUrl(
    url: String?,
    placeholder: Drawable?,
) {
this.setImageDrawable(placeholder)
    if(url!=null)
    CoroutineScope(Dispatchers.IO).launch {
        runCatching{
            try {
                this@bindSrcUrl.tag = url
                val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.useCaches = true
                connection.connect()
                val input: InputStream = connection.inputStream
                val b = BitmapFactory.decodeStream(input)
                withContext(Dispatchers.Main) {
                    if(this@bindSrcUrl.tag == url)
                    this@bindSrcUrl.setImageBitmap(b)
                }
            } catch (e: Exception) {
                Log.e("Exception", e.message.toString())
            }
        }
    }
}
