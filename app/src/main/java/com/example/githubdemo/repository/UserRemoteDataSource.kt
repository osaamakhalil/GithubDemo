package com.example.githubdemo.repository

import com.example.githubdemo.api.RetrofitBuilder
import com.example.githubdemo.users.model.*


class UserRemoteDataSource(private val retrofitBuilder: RetrofitBuilder) {

    suspend fun getUsers(page: Int): List<UserResponse> {
        return retrofitBuilder.userApiService.getUsers(page)
    }

    suspend fun getUsersDetails(userName: String): UserDetails {
        return retrofitBuilder.userApiService.getUsersDetails(userName)
    }

    suspend fun getUserRepo(userName: String, page: Int): MutableList<UserRepo> {
        return retrofitBuilder.userApiService.getUserRepo(userName, page)
    }

    suspend fun getUserFollowing(userName: String, page: Int): MutableList<UserResponse> {
        return retrofitBuilder.userApiService.getUserFollowing(userName, page)
    }

    suspend fun getUserFollowers(userName: String, page: Int): MutableList<UserResponse> {
        return retrofitBuilder.userApiService.getUserFollowers(userName, page)
    }

    suspend fun getUserStarred(userName: String, per_page: Int): List<UserRepo> {
        return retrofitBuilder.userApiService.getUserStarred(userName, per_page)
    }

    suspend fun searchForUser(userName: String, page: Int): UsersSearch {
        return retrofitBuilder.userApiService.searchForUser(userName, page)
    }

    suspend fun getPublicRepos(page: Int, repoName: String): PublicRepos {
        return retrofitBuilder.userApiService.getPublicRepos(page, repoName)
    }

    suspend fun getPublicIssues(page: Int, IssueName: String): PublicIssues {
        return retrofitBuilder.userApiService.getPublicIssue(page, IssueName)
    }

}