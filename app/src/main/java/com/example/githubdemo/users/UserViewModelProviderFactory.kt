package com.example.githubdemo.users

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.repository.UserRepositoryImpl
import java.lang.IllegalArgumentException

class UserViewModelProviderFactory(private val userRepository: UserRepositoryImpl, val networkUtil: NetworkUtil) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository, networkUtil) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}