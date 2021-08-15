package com.example.githubdemo.repository

import com.example.githubdemo.users.model.UserResponse


class UserRepositoryImpl : UserRepository {
    private val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSource()

    override suspend fun getUsers(page:Int): List<UserResponse> {
        return userRemoteDataSource.getUsers(page)
    }

}