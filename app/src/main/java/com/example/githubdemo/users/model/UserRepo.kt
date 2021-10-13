package com.example.githubdemo.users.model

import com.google.gson.annotations.SerializedName

data class UserRepo(
    @SerializedName("name")
    val repoName: String,
    @SerializedName("stargazers_count")
    val starsCount: Int,
    @SerializedName("forks_count")
    val forksCount: Int,

    ) {
}