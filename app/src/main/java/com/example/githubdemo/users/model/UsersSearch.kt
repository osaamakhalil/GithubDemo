package com.example.githubdemo.users.model

import com.google.gson.annotations.SerializedName


data class UsersSearch (
    @SerializedName("total_count")
    var totalCount : Int,
    @SerializedName("incomplete_results")
    var incompleteResults : Boolean,
    @SerializedName("items")
    var userResponse : List<UserResponse>
)