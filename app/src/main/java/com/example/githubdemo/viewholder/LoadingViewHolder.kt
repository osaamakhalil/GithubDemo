package com.example.githubdemo.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.adapter.UserAdapter
import com.example.githubdemo.api.UserApiStatus
import com.example.githubdemo.databinding.PageProgressBarBinding
import com.example.githubdemo.users.UserFragment
import okhttp3.internal.notify

class LoadingViewHolder(
    private val binding: PageProgressBarBinding,
val    userFragment: UserFragment
) :
    RecyclerView.ViewHolder(binding.root) {
    val pp = binding.pageProgress
//    val tv = binding.tvNoInternet
//    val bt = binding.btTryAgain


//    fun bind() {
//        if (userFragment.noInternet) {
//            pp.visibility = View.GONE
//            tv.visibility = View.VISIBLE
//            bt.visibility = View.VISIBLE
//        } else {
//            pp.visibility = View.VISIBLE
//            tv.visibility = View.GONE
//            bt.visibility = View.GONE
//        }
//    }
}

fun showLoadingView(viewHolder: LoadingViewHolder, position: Int, userFragment: UserFragment) {
   // viewHolder.bind()
//    viewHolder.apply {
//       if (userFragment.noInternet) {
//            pp.visibility = View.GONE
////            tv.visibility = View.VISIBLE
////            bt.visibility = View.VISIBLE
//        } else {
//            pp.visibility = View.VISIBLE
////            tv.visibility = View.GONE
////            bt.visibility = View.GONE
//       }
//    }
}
