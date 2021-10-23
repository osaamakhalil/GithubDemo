package com.example.githubdemo.users.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.utils.NetworkUtil
import java.lang.IllegalArgumentException


@Suppress("UNCHECKED_CAST")
class PublicIssuesViewModelProviderFactory(
    private val networkUtil: NetworkUtil,
    private val userRepository: UserRepositoryImpl,
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicIssuesViewModel::class.java)) {
            return PublicIssuesViewModel(networkUtil,userRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }

}