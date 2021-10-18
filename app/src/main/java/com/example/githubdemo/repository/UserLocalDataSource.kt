package com.example.githubdemo.repository

import androidx.lifecycle.LiveData
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.model.UserResponse

class UserLocalDataSource(private val usersDatabase: UsersDatabase) {

    suspend fun insertUser(user: UserResponse): Long {
        return usersDatabase.getUserDao().insert(user)
    }

    fun getAllUsers(): LiveData<List<UserResponse>> {
        return usersDatabase.getUserDao().getAllUsers()
    }

    fun getUserName(name:String): LiveData<List<UserResponse>> {
        return usersDatabase.getUserDao().getUserName(name)
    }

    suspend fun deleteUser(user: UserResponse) {
        return usersDatabase.getUserDao().deleteUser(user)
    }

}
