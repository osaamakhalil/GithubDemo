package com.example.githubdemo.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.databinding.RepoItemBinding
import com.example.githubdemo.users.model.UserRepo

class RepoViewHolder(private val binding: RepoItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(userRepo: UserRepo) {
        binding.apply {
            tvUserRepo.text = userRepo.repoName
            tvStarsCount.text = userRepo.starsCount.toString()
            tvForkCount.text = userRepo.forksCount.toString()
        }

    }
}