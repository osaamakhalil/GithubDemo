package com.example.githubdemo.users.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class UserResponse(
    @SerializedName("login")
    val name: String,
    @SerializedName("avatar_url")
    val imageUrl: String,
    @SerializedName("id")
    @PrimaryKey
    val id: Int,
) : Parcelable
