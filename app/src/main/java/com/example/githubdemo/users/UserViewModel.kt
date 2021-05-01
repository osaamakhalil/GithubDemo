package com.example.githubdemo.users

import androidx.lifecycle.ViewModel
import com.example.githubdemo.repository.UserRepository

class UserViewModel(val userRepository: UserRepository) : ViewModel() {
}