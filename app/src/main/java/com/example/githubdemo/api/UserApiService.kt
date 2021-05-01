package com.example.githubdemo.api

import com.example.githubdemo.users.model.UserResponse
import retrofit2.http.GET

interface UserApiService {

    @GET("users")
    fun getUsers(): List<UserResponse>
}