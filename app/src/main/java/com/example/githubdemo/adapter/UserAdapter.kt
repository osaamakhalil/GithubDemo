package com.example.githubdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.databinding.ItemUserBinding
import com.example.githubdemo.databinding.NetworkStatusBinding
import com.example.githubdemo.users.home.UserViewModel
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Constant.Companion.USER_LIST_VIEW
import com.example.githubdemo.utils.Constant.Companion.VIEW_TYPE_LOADING
import com.example.githubdemo.viewholder.NetworkStatusViewHolder
import com.example.githubdemo.viewholder.UserViewHolder
import com.example.githubdemo.viewholder.showLoadingView


class UserAdapter(
    private val networkUtil: NetworkUtil,
    private val onItemClicked: (UserResponse) -> Unit,
    private val onTryAgainClick: () -> Unit
) :
    ListAdapter<UserResponse, RecyclerView.ViewHolder>(UserDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == USER_LIST_VIEW) {
            val binding: ItemUserBinding =
                ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return UserViewHolder(binding, onItemClicked)
        } else {
            val pageProgressBarBinding: NetworkStatusBinding =
                NetworkStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NetworkStatusViewHolder(
                pageProgressBarBinding,
                networkUtil,
                onTryAgainClick
            )
        }

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is UserViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is NetworkStatusViewHolder) {
            showLoadingView(viewHolder)
        }
    }

    override fun submitList(list: List<UserResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1)
            VIEW_TYPE_LOADING
        else
            USER_LIST_VIEW
    }

    private fun populateItemRows(viewHolder: UserViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.bind(item)
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class UserDiffCallBack : DiffUtil.ItemCallback<UserResponse>() {
    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem == newItem
    }

}