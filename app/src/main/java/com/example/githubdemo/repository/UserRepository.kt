package com.example.githubdemo.repository

import com.example.githubdemo.users.model.UserResponse

interface UserRepository {

    suspend fun getUsers(page:Int) : List<UserResponse>
}