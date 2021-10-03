package com.example.githubdemo.users.model

import com.google.gson.annotations.SerializedName

data class UserRepo(
    @SerializedName("name")
    val repoName: String,
) {
}