package com.example.githubdemo.users

import androidx.lifecycle.ViewModel
import com.example.githubdemo.repository.UserRepositoryImpl

class UserViewModel(val userRepository: UserRepositoryImpl) : ViewModel() {
}