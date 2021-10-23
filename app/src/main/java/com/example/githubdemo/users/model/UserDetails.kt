package com.example.githubdemo.users.model

import com.google.gson.annotations.SerializedName

data class UserDetails(
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("following")
    val following: Int,
    @SerializedName("name")
    val realName: String,
    @SerializedName("bio")
    val bio: String,
    @SerializedName("blog")
    val blog: String,
    @SerializedName("public_repos")
    val numOfRepo: Int
)