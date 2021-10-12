package com.example.githubdemo.repository

import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.users.model.UserResponse

interface UserRepository {

    suspend fun getUsers(page: Int): List<UserResponse>

    suspend fun getUsersDetails(userName: String): UserDetails

    suspend fun getUserRepo(userName: String): List<UserRepo>

    suspend fun getUserFollowing(userName: String,page: Int): MutableList<UserResponse>
}