package com.example.githubdemo.repository

import com.example.githubdemo.api.RetrofitBuilder
import com.example.githubdemo.users.model.Search
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

    suspend fun getUserRepo(userName: String, per_page: Int): List<UserRepo> {
        return RetrofitBuilder.userApiService.getUserRepo(userName, per_page)
    }

    suspend fun getUserFollowing(userName: String, page: Int): MutableList<UserResponse> {
        return RetrofitBuilder.userApiService.getUserFollowing(userName, page)
    }

    suspend fun getUserFollowers(userName: String, page: Int): MutableList<UserResponse> {
        return RetrofitBuilder.userApiService.getUserFollowers(userName, page)
    }

    suspend fun getUserStarred(userName: String, per_page: Int): List<UserRepo> {
        return RetrofitBuilder.userApiService.getUserStarred(userName, per_page)
    }

    suspend fun searchForUser(userName: String, page: Int): Search {
        return RetrofitBuilder.userApiService.searchForUser(userName, page)
    }

}