package com.example.githubdemo.repository

import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.users.model.UserResponse


class UserRepositoryImpl : UserRepository {
    private val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSource()

    override suspend fun getUsers(page:Int): List<UserResponse> {
        return userRemoteDataSource.getUsers(page)
    }
    override suspend fun getUsersDetails(userName: String): UserDetails {
        return userRemoteDataSource.getUsersDetails(userName)
    }

    override suspend fun getUserRepo(userName: String): List<UserRepo> {
        return userRemoteDataSource.getUserRepo(userName)
    }


}