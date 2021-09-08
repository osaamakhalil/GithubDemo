package com.example.githubdemo.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build

enum class UserApiStatus { LOADING, ERROR, DONE, NO_INTERNET_CONNECTION,PAGE_LOADING }

class NetworkUtil(private val context: Context) {
        fun hasInternetConnection(): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                        ?: return false
                return when {
                    capabilities.hasTransport(TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
            return false
        }
}
