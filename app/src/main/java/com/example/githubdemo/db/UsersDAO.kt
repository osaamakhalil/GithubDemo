package com.example.githubdemo.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubdemo.users.model.UserResponse

@Dao
interface UsersDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserResponse): Long

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<UserResponse>>

    @Query("SELECT * FROM users WHERE name LIKE :name ")
    fun getUserName(name: String): LiveData<List<UserResponse>>

    @Delete
    suspend fun deleteUser(user: UserResponse)

}