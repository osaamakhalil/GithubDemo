package com.example.githubdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubdemo.databinding.ItemUserBinding
import com.example.githubdemo.databinding.PageProgressBarBinding
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Constant.Companion.USER_LIST_VIEW
import com.example.githubdemo.utils.Constant.Companion.VIEW_TYPE_LOADING
import com.example.githubdemo.viewholder.LoadingViewHolder
import com.example.githubdemo.viewholder.showLoadingView


class UserAdapter() : ListAdapter<UserResponse, RecyclerView.ViewHolder>(differCallback) {


    //helper class for computing the difference between two lists via DiffUtil on a background thread
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == USER_LIST_VIEW) {
            val binding: ItemUserBinding =
                ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return UserViewHolder(binding)
        } else {
            val binding: PageProgressBarBinding =
                PageProgressBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadingViewHolder(binding)
        }

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is UserViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == differ.currentList.size - 1)
            VIEW_TYPE_LOADING
        else
            USER_LIST_VIEW
    }


    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserResponse) {
            binding.apply {
                tvUserName.text = user.name
                Glide.with(itemView.context)
                    .load(user.imageUrl)
                    .circleCrop()
                    .into(ivUserAvatar)
            }
        }
    }


    private fun populateItemRows(viewHolder: UserViewHolder, position: Int) {
        val item = differ.currentList[position]
        viewHolder.bind(item)
    }

}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
private val differCallback = object : DiffUtil.ItemCallback<UserResponse>() {
    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem == newItem
    }

}