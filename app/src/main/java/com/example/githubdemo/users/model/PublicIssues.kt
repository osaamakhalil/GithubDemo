package com.example.githubdemo.users.model

import com.google.gson.annotations.SerializedName

data class PublicIssues(
    @SerializedName("total_count") var totalCount: Int,
    @SerializedName("incomplete_results") var incompleteResults: Boolean,
    @SerializedName("items") var issuesItems: List<IssuesItem>,
)

data class IssuesItem(
    @SerializedName("number") var number: Int,
    @SerializedName("title") var title: String,
    @SerializedName("user") var user: UserResponse,
    @SerializedName("state") var state: String,
)
