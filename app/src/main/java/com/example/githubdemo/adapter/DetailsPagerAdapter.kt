package com.example.githubdemo.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.githubdemo.users.detail.ui.FollowersFragment
import com.example.githubdemo.users.detail.ui.FollowingFragment
import com.example.githubdemo.users.detail.ui.RepositoryFragment
import com.example.githubdemo.utils.Constant.Companion.USER_NAME_KEY

class DetailsPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
     val userName: String
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            0 -> RepositoryFragment().apply {
                arguments = bundleOf(USER_NAME_KEY to userName)
            }
            1 -> FollowingFragment().apply {
                arguments = bundleOf(USER_NAME_KEY to userName)
            }
            2 -> FollowersFragment()
            else -> RepositoryFragment()
        }
    }
}