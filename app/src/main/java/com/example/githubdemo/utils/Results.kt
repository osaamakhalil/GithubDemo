package com.example.githubdemo.utils


sealed class Results<out T>(
    val data: T? = null,
    val message: String? = null

) {
    object Loading : Results<Nothing>()
    object NoInternet: Results<Nothing>()
    class Success<out T>(data: T?) : Results<T>(data)
    class Error<out T>(message: String? = null, data: T? = null) : Results<T>(data, message)
}

