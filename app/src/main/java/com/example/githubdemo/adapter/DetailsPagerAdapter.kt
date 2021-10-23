package com.example.githubdemo.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.githubdemo.ui.UsersRepositoryFragment
import com.example.githubdemo.ui.StarredFragment
import com.example.githubdemo.utils.Constant.Companion.USER_NAME_KEY

class DetailsPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    val userName: String,
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            0 -> UsersRepositoryFragment().apply {
                arguments = bundleOf(USER_NAME_KEY to userName)
            }
            1 -> StarredFragment().apply {
                arguments = bundleOf(USER_NAME_KEY to userName)
            }
            else -> UsersRepositoryFragment()
        }
    }
}