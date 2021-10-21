package com.example.githubdemo.repository

import androidx.lifecycle.LiveData
import com.example.githubdemo.users.model.Search
import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.users.model.UserResponse

interface UserRepository {

    suspend fun getUsers(page: Int): List<UserResponse>

    suspend fun getUsersDetails(userName: String): UserDetails

    suspend fun getUserRepo(userName: String, per_page: Int): List<UserRepo>

    suspend fun getUserFollowing(userName: String, page: Int): MutableList<UserResponse>

    suspend fun getUserFollowers(userName: String, page: Int): MutableList<UserResponse>

    suspend fun getUserStarred(userName: String, per_page: Int): List<UserRepo>

    suspend fun searchForUser(userName: String, page: Int): Search

    suspend fun insertUser(user: UserResponse): Long

    fun getAllUsers(): LiveData<List<UserResponse>>

    fun getUserName(name: String): LiveData<List<UserResponse>>

    suspend fun deleteUser(user: UserResponse)
}