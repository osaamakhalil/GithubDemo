package com.example.githubdemo.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.databinding.PageProgressBarBinding


class LoadingViewHolder(private val binding: PageProgressBarBinding) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.pageProgress
    }
}
fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {}
