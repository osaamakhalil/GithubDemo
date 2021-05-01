package com.example.githubdemo.repository

import com.example.githubdemo.api.RetrofitBuilder
import com.example.githubdemo.users.model.UserResponse
import javax.sql.DataSource

class UserRepositoryImpl : UserRepository {

  private val userDataSource: UserDataSource = UserDataSource()

    override suspend fun getUsers(): List<UserResponse> {
        return userDataSource.getUsers()
    }

}