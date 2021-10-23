package com.example.githubdemo.api

import com.example.githubdemo.users.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("since") page: Int,
    ): List<UserResponse>

    @GET("/users/{username}")
    suspend fun getUsersDetails(
        @Path("username") userName: String,
    ): UserDetails

    @GET("/users/{username}/repos")
    suspend fun getUserRepo(
        @Path("username") userName: String,
        @Query("page") page: Int,
    ): MutableList<UserRepo>

    @GET("/users/{username}/following")
    suspend fun getUserFollowing(
        @Path("username") userName: String,
        @Query("page") page: Int,
    ): MutableList<UserResponse>

    @GET("/users/{username}/followers")
    suspend fun getUserFollowers(
        @Path("username") userName: String,
        @Query("page") page: Int,
    ): MutableList<UserResponse>

    @GET("/users/{username}/starred")
    suspend fun getUserStarred(
        @Path("username") userName: String,
        @Query("per_page") per_page: Int,
    ): List<UserRepo>

    @GET("/search/users")
    suspend fun searchForUser(
        @Query("q") userName: String,
        @Query("page") page: Int,
    ): UsersSearch

    @GET("/search/repositories")
    suspend fun getPublicRepos(
        @Query("page") page: Int,
        @Query("q") repoName: String
        ): PublicRepos

    @GET("/search/issues")
    suspend fun getPublicIssue(
        @Query("page") page: Int,
        @Query("q") IssueName: String
        ): PublicIssues
}