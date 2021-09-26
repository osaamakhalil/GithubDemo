package com.example.githubdemo.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubdemo.databinding.ItemUserBinding
import com.example.githubdemo.users.model.UserResponse

class UserViewHolder(private val binding: ItemUserBinding,private val onItemCLicked:()->Unit) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(user: UserResponse) {
        binding.apply {
            tvUserName.text = user.name
            Glide.with(itemView.context)
                .load(user.imageUrl)
                .circleCrop()
                .into(ivUserAvatar)
            userItem.setOnClickListener {
                onItemCLicked()
            }
        }
    }
}

