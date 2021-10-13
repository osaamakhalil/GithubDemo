package com.example.githubdemo.users.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    @SerializedName("login")
    val name: String,
    @SerializedName("avatar_url")
    val imageUrl: String,
    @SerializedName("id")
    val id: Int
) : Parcelable
