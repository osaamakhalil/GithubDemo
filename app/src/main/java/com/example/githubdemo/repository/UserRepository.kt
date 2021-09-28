package com.example.githubdemo.repository

import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserResponse

interface UserRepository {

    suspend fun getUsers(page:Int) : List<UserResponse>

    suspend fun getUsersDetails(userName:String) : UserDetails
}