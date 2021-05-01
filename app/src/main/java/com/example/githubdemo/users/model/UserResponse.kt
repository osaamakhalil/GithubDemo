package com.example.githubdemo.users.model

import com.google.gson.annotations.SerializedName

data class UserResponse(@SerializedName("login") val name: String
                        , @SerializedName("avatar_url") val imageUrl: String)