package com.example.githubdemo.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.utils.NetworkUtil
import java.lang.IllegalArgumentException

class BookMarkViewModelFactory(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookMarkViewModel::class.java)) {
            return BookMarkViewModel(userRepository, networkUtil) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}
