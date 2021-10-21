package com.example.githubdemo.repository

import androidx.lifecycle.LiveData
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.model.Search
import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.users.model.UserResponse


class UserRepositoryImpl(db: UsersDatabase) : UserRepository {

    private val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSource()
    private val userLocalDataSource: UserLocalDataSource = UserLocalDataSource(db)

    override suspend fun getUsers(page: Int): List<UserResponse> {
        return userRemoteDataSource.getUsers(page)
    }

    override suspend fun getUsersDetails(userName: String): UserDetails {
        return userRemoteDataSource.getUsersDetails(userName)
    }

    override suspend fun getUserRepo(userName: String, per_page: Int): List<UserRepo> {
        return userRemoteDataSource.getUserRepo(userName, per_page)
    }

    override suspend fun getUserFollowing(userName: String, page: Int): MutableList<UserResponse> {
        return userRemoteDataSource.getUserFollowing(userName, page)
    }

    override suspend fun getUserFollowers(userName: String, page: Int): MutableList<UserResponse> {
        return userRemoteDataSource.getUserFollowers(userName, page)
    }

    override suspend fun getUserStarred(userName: String, per_page: Int): List<UserRepo> {
        return userRemoteDataSource.getUserStarred(userName, per_page)
    }

    override suspend fun searchForUser(userName: String, page: Int): Search {
        return userRemoteDataSource.searchForUser(userName, page)
    }

    override suspend fun insertUser(user: UserResponse): Long {
        return userLocalDataSource.insertUser(user)
    }

    override fun getAllUsers(): LiveData<List<UserResponse>> {
        return userLocalDataSource.getAllUsers()
    }

    override fun getUserName(name: String): LiveData<List<UserResponse>> {
        return userLocalDataSource.getUserName(name)
    }

    override suspend fun deleteUser(user: UserResponse) {
        return userLocalDataSource.deleteUser(user)
    }

}