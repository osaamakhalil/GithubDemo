package com.example.githubdemo.users.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.githubdemo.R
import com.example.githubdemo.adapter.DetailsPagerAdapter
import com.example.githubdemo.databinding.FragmentUserDetailsBinding
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailsFragment : Fragment() {
    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false)


        setupDetailPagerView()


        // Inflate the layout for this fragment
        return binding.root
    }


    private fun setupDetailPagerView() {
        val fragmentPagerList = arrayListOf(
            RepositoryFragment(),
            FollowingFragment(),
            FollowersFragment()
        )
        val adapter = DetailsPagerAdapter(
            fragmentPagerList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        val pager = binding.pager
        val tabLayout = binding.tabLayout
        pager.adapter = adapter
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when(position){
                0 -> tab.text = "Repository"
                1 -> tab.text = "Following"
                2 -> tab.text = "Followers"
            }
        }.attach()

    }

}