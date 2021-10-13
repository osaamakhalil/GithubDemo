package com.example.githubdemo.users.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.repository.UserRepositoryImpl
import java.lang.IllegalArgumentException

class DetailsViewModelProviderFactory(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(userRepository, networkUtil) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }

}