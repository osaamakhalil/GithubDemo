package com.example.githubdemo.utils

sealed class Results<T>(
    val data: T? = null,
    val message: String? = null

) {
    class Loading<T> : Results<T>()
    class Success<T>(data: T?) : Results<T>(data)
    class Error<T>(message: String? = null, data: T? = null) : Results<T>(data, message)
}

