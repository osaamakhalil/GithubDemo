package com.example.githubdemo.users.publicrepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.utils.NetworkUtil
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class PublicRepoViewModelProviderFactory(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicRepoViewModel::class.java)) {
            return PublicRepoViewModel(userRepository, networkUtil) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }

}