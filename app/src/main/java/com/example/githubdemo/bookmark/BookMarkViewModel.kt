package com.example.githubdemo.bookmark

import androidx.lifecycle.ViewModel
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.utils.NetworkUtil

class BookMarkViewModel(
    val userRepositoryImpl: UserRepositoryImpl,
    val networkUtil: NetworkUtil,
) : ViewModel() {

    fun getAllUsers() = userRepositoryImpl.getAllUsers()
}