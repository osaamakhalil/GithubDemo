package com.example.githubdemo.api

import com.example.githubdemo.users.model.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("per_page") page:Int
    ): List<UserResponse>
}