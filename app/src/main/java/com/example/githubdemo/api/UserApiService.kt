package com.example.githubdemo.api

import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("since") page:Int
    ): List<UserResponse>

    @GET("/users/{username}")
    suspend fun getUsersDetails(
        @Path("username") userName: String,
    ): UserDetails
}