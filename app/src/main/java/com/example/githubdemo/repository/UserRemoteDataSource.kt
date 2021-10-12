package com.example.githubdemo.repository

import com.example.githubdemo.api.RetrofitBuilder
import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.users.model.UserResponse


class UserRemoteDataSource {
    suspend fun getUsers(page: Int): List<UserResponse> {
        return RetrofitBuilder.userApiService.getUsers(page)
    }

    suspend fun getUsersDetails(userName: String): UserDetails {
        return RetrofitBuilder.userApiService.getUsersDetails(userName)
    }

    suspend fun getUserRepo(userName: String): List<UserRepo> {
        return RetrofitBuilder.userApiService.getUserRepo(userName)
    }

    suspend fun getUserFollowing(userName: String, page: Int): MutableList<UserResponse> {
        return RetrofitBuilder.userApiService.getUserFollowing(userName, page)
    }
}