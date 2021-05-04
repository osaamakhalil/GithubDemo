package com.example.githubdemo.repository

import com.example.githubdemo.api.RetrofitBuilder
import com.example.githubdemo.users.model.UserResponse



class UserDataSource {
        suspend fun getUsers(): List<UserResponse> {
              return RetrofitBuilder.userApiService.getUsers()

        }

}