package com.example.githubdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.databinding.NetworkStatusBinding
import com.example.githubdemo.databinding.RepoItemBinding
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.utils.Constant.Companion.REPO_LIST_VIEW
import com.example.githubdemo.utils.Constant.Companion.VIEW_TYPE_LOADING
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.viewholder.NetworkStatusViewHolder
import com.example.githubdemo.viewholder.RepoViewHolder
import com.example.githubdemo.viewholder.showLoadingView

class UserRepoAdapter(
    private val networkUtil: NetworkUtil,
    private val onTryAgainClick: () -> Unit,
) : ListAdapter<UserRepo, RecyclerView.ViewHolder>(UserRepoDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == REPO_LIST_VIEW) {
            val binding: RepoItemBinding =
                RepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RepoViewHolder(binding)
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


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RepoViewHolder) {
            populateItemRows(holder, position)
        } else if (holder is NetworkStatusViewHolder) {
            showLoadingView(holder)
        }
    }

    override fun submitList(list: List<UserRepo>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading(position))
            VIEW_TYPE_LOADING
        else
            REPO_LIST_VIEW
    }

    private fun isLoading(position: Int): Boolean {
        return position == itemCount - 1 && !networkUtil.lastPage && itemCount >= 6
    }

    private fun populateItemRows(viewHolder: RepoViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.bind(item)
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