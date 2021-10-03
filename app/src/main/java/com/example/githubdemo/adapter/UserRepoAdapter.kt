package com.example.githubdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.githubdemo.databinding.RepoItemBinding
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.viewholder.RepoViewHolder

class UserRepoAdapter : ListAdapter<UserRepo, RepoViewHolder>(UserRepoDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding: RepoItemBinding =
            RepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding)

    }


    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


}


class UserRepoDiffCallBack : DiffUtil.ItemCallback<UserRepo>() {
    override fun areItemsTheSame(oldItem: UserRepo, newItem: UserRepo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserRepo, newItem: UserRepo): Boolean {
        return oldItem == newItem
    }


}