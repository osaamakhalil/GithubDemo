package com.example.githubdemo.users.detail.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
    private lateinit var usersName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        usersName = args.userResponse.name

        detailsViewModel.getUserDetails(usersName)

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
        navigateToFollowScreen()
        tryAgain(usersName)
        setupDetailPagerView()
        navigateToBack()
    }


    private fun headersDetailsViews(response: Results<UserDetails>) {
        val userResponse = args.userResponse
        binding.apply {
            realName.text = response.data?.realName
            followersCount.text = response.data?.followers.toString()
            followingCount.text = response.data?.following.toString()
            userBio.text = response.data?.bio
            userBlog.text = response.data?.blog
            userName.text = userResponse.name
            followersText.visibility = View.VISIBLE
            followingText.visibility = View.VISIBLE
            blogIcon.visibility = View.VISIBLE
            followIcon.visibility = View.VISIBLE

            if (response.data?.blog == "") {
                blogIcon.visibility = View.GONE
            }
        }
        Glide.with(this)
            .load(userResponse.imageUrl)
            .circleCrop()
            .into(binding.userImage)
    }

    private fun navigateToFollowScreen() {
        binding.apply {
            followingText.setOnClickListener {
                findNavController().navigate(UserDetailsFragmentDirections.fromUserDetailsToFollowingFragment(
                    usersName))
            }
            followersText.setOnClickListener {
                findNavController().navigate(UserDetailsFragmentDirections.fromUserDetailsToFollowersFragment(
                    usersName))
            }
        }
    }

    private fun navigateToBack() {
        binding.detailsBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
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