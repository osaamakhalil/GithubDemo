package com.example.githubdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubdemo.R
import com.example.githubdemo.users.model.UserResponse

class UserAdapter : ListAdapter<UserResponse, UserAdapter.ViewHolder>(UserDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        private val userAvatar: ImageView = itemView.findViewById(R.id.iv_user_avatar)

        fun bind(user: UserResponse) {
            userName.text = user.name
            Glide.with(itemView.context)
                .load(user.imageUrl)
                .placeholder(R.drawable.loading_animation)
                .into(userAvatar)
        }
    }
}


class UserDiffCallBack : DiffUtil.ItemCallback<UserResponse>() {
    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem == newItem
    }

}