package com.example.githubdemo.users.detail.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.githubdemo.R
import com.example.githubdemo.adapter.DetailsPagerAdapter
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.databinding.FragmentUserDetailsBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.detail.DetailsViewModel
import com.example.githubdemo.users.detail.DetailsViewModelProviderFactory
import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.utils.Results
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: UserDetailsFragmentArgs by navArgs()
    private lateinit var detailsViewModel: DetailsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = UserRepositoryImpl()
        val application = requireActivity().application
        val networkUtil = NetworkUtil(application)
        val viewModelFactory = DetailsViewModelProviderFactory(repository, networkUtil)
        detailsViewModel =
            ViewModelProvider(this, viewModelFactory).get(DetailsViewModel::class.java)

        val userName = args.userResponse.name

        detailsViewModel.getUserDetails(userName)

        detailsViewModel.usersDetailsStatus.observe(viewLifecycleOwner) { UserFollowCount ->
            UserFollowCount?.let { response ->
                when (response) {
                    is Results.Error -> {
                        serverErrorView(true)
                        tryAgainView(true)
                        noInternetView(false)
                        progressbarView(false)
                    }
                    Results.Loading -> {
                        progressbarView(true)
                        serverErrorView(false)
                        noInternetView(false)
                        tryAgainView(false)
                    }
                    Results.NoInternet -> {
                        tryAgainView(true)
                        noInternetView(true)
                        serverErrorView(false)
                        progressbarView(false)
                    }
                    is Results.Success -> {
                        tryAgainView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        progressbarView(false)
                        headersDetailsViews(response)
                    }
                }
            }
        }
        tryAgain(userName)
        setupDetailPagerView()
    }


    private fun headersDetailsViews(response: Results<UserDetails>) {
        val userResponse = args.userResponse
        binding.apply {
            realName.text = response.data?.realName
            followersCount.text = response.data?.followers.toString()
            followingCount.text = response.data?.following.toString()
            userName.text = userResponse.name
            followersText.visibility = View.VISIBLE
            followingText.visibility = View.VISIBLE
        }
        Glide.with(this)
            .load(userResponse.imageUrl)
            .circleCrop()
            .into(binding.userImage)
    }


    private fun tryAgain(userName: String) {
        binding.tryAgainDetails.setOnClickListener {
            detailsViewModel.getUserDetails(userName)
        }
    }

    private fun setupDetailPagerView() {
        val adapter = DetailsPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle,
            args.userResponse.name
        )
        val pager = binding.pager
        val tabLayout = binding.tabLayout
        pager.adapter = adapter
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Repository"
                1 -> tab.text = "Starred"
            }
        }.attach()

    }

    private fun progressbarView(showViews: Boolean) {
        binding.detailsProgress.isVisible = showViews
    }

    private fun noInternetView(showViews: Boolean) {
        binding.noInternetConnectionDetails.isVisible = showViews

    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivServerErrorDetails.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.tryAgainDetails.isVisible = showViews
    }
}