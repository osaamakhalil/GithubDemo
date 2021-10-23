package com.example.githubdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.githubdemo.databinding.IssuesItemBinding
import com.example.githubdemo.databinding.NetworkStatusBinding
import com.example.githubdemo.users.model.IssuesItem
import com.example.githubdemo.utils.Constant.Companion.ISSUES_LIST_VIEW
import com.example.githubdemo.utils.Constant.Companion.VIEW_TYPE_LOADING
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.viewholder.IssuesViewHolder
import com.example.githubdemo.viewholder.NetworkStatusViewHolder
import com.example.githubdemo.viewholder.showLoadingView

class IssuesAdapter(
    private val networkUtil: NetworkUtil,
    private val onTryAgainClick: () -> Unit,
) : ListAdapter<IssuesItem, RecyclerView.ViewHolder>(IssueDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ISSUES_LIST_VIEW) {
            val binding: IssuesItemBinding =
                IssuesItemBinding.inflate(LayoutInflater.from(parent.context),
                    parent, false)
            return IssuesViewHolder(binding)
        } else {
            val binding: NetworkStatusBinding =
                NetworkStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NetworkStatusViewHolder(binding, networkUtil, onTryAgainClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IssuesViewHolder){
            val item = getItem(position)
            holder.bind(item)
        }else if (holder is NetworkStatusViewHolder){
            showLoadingView(holder)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1){
            return VIEW_TYPE_LOADING
        }else{
            return ISSUES_LIST_VIEW
        }
    }

}


/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `
 * submitList`.
 */
class IssueDiffCallBack : DiffUtil.ItemCallback<IssuesItem>() {
    override fun areItemsTheSame(oldIssuesItem: IssuesItem, newIssuesItem: IssuesItem): Boolean {
        return oldIssuesItem == newIssuesItem
    }

    override fun areContentsTheSame(oldIssuesItem: IssuesItem, newIssuesItem: IssuesItem): Boolean {
        return oldIssuesItem == newIssuesItem
    }

}