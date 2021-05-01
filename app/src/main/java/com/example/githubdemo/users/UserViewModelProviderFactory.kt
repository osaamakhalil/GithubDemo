package com.example.githubdemo.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.repository.UserRepositoryImpl
import java.lang.IllegalArgumentException

class UserViewModelProviderFactory(private val userRepository: UserRepositoryImpl) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}