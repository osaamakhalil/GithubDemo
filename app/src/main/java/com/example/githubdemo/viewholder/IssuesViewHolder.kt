package com.example.githubdemo.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubdemo.databinding.IssuesItemBinding
import com.example.githubdemo.users.model.IssuesItem


class IssuesViewHolder(private val binding: IssuesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(issue: IssuesItem) {
        binding.apply {
            Glide.with(itemView.context)
                .load(issue.user.imageUrl)
                .circleCrop()
                .into(ivUserAvatar)
            tvUserName.text = issue.user.name
            title.text = issue.title
            state.text = issue.state
            number.text = issue.number.toString()
        }
    }
}