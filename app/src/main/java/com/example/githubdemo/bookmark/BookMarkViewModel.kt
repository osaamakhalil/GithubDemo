package com.example.githubdemo.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookMarkViewModel(
    val userRepositoryImpl: UserRepositoryImpl,
    val networkUtil: NetworkUtil,
) : ViewModel() {

    fun getAllUsers() = userRepositoryImpl.getAllUsers()

    fun deleteUser(user: UserResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepositoryImpl.deleteUser(user)
        }
    }
    fun saveUser(user: UserResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepositoryImpl.insertUser(user)
        }
    }
}
