package com.example.githubdemo.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.databinding.NetworkStatusBinding
import com.example.githubdemo.users.UserFragment

class NetworkStatusViewHolder(
    private val binding: NetworkStatusBinding,
    private val onTryAgainClick: () -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    val pagingProgress = binding.pagingProgress
    val tvNoInternet = binding.noInternetConnection
    val btTryAgain = binding.tryAgain
    fun bind() {
        btTryAgain.setOnClickListener {
            onTryAgainClick()
        }
    }

}

fun showLoadingView(viewHolder: NetworkStatusViewHolder, position: Int, userFragment: UserFragment) {
    viewHolder.bind()
    viewHolder.apply {
        if (userFragment.noInternet) {

            pagingProgress.visibility = View.GONE
            btTryAgain.visibility = View.VISIBLE
            tvNoInternet.visibility = View.VISIBLE
        } else {
            pagingProgress.visibility = View.VISIBLE
            btTryAgain.visibility = View.GONE
            tvNoInternet.visibility = View.GONE
        }
    }
}
