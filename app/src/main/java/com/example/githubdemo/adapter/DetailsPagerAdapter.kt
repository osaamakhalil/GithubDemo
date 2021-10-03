package com.example.githubdemo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.githubdemo.users.detail.ui.FollowersFragment
import com.example.githubdemo.users.detail.ui.FollowingFragment
import com.example.githubdemo.users.detail.ui.RepositoryFragment

class DetailsPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val userName: String
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RepositoryFragment(userName)
            1 -> FollowingFragment()
            2 -> FollowersFragment()
            else -> RepositoryFragment(userName)
        }
    }
}