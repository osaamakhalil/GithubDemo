package com.example.githubdemo.users.model

import com.google.gson.annotations.SerializedName

data class PublicRepos(
    @SerializedName("total_count")
    var totalCount : Int,
    @SerializedName("incomplete_results")
    var incompleteResults : Boolean,
    @SerializedName("items")
    var userRepo : List<UserRepo>
)
