package com.example.githubdemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.ListUserAdapter
import com.example.githubdemo.databinding.FragmentFollowersBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.detail.DetailsViewModel
import com.example.githubdemo.users.detail.DetailsViewModelProviderFactory
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.utils.Results


class FollowersFragment : Fragment() {

    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!
    private lateinit var userFollowingAdapter: ListUserAdapter
    private var isScrolling = false
    private val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    private val detailsViewModel: DetailsViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        DetailsViewModelProviderFactory(repository, networkUtil)
    }
    private val args: FollowersFragmentArgs by navArgs()
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_followers, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = args.userName

        detailsViewModel.getUserFollowers(userName)
        detailsViewModel.getUserDetails(userName)


        userFollowersListResultsHandling()
        setUpRecyclerView(networkUtil)
        tryAgainButton()
        navigateToBack()
    }

    private fun userFollowersListResultsHandling() {
        detailsViewModel.usersFollowersStatus.observe(viewLifecycleOwner) { userFollowing ->
            userFollowing?.let { response ->
                when (response) {
                    is Results.Error -> {
                        serverErrorView(true)
                        tryAgainView(true)
                        noInternetView(false)
                        progressBarView(false)
                    }
                    Results.Loading -> {
                        progressBarView(true)
                        noInternetView(false)
                        tryAgainView(false)
                        serverErrorView(false)
                    }
                    Results.NoInternet -> {
                        noInternetView(true)
                        tryAgainView(true)
                        progressBarView(false)
                        serverErrorView(false)
                    }
                    is Results.Success -> {
                        serverErrorView(false)
                        noInternetView(false)
                        tryAgainView(false)
                        progressBarView(false)
                        userFollowingAdapter.submitList(response.data)
                        response.data?.let { userFollowingAdapter.notifyItemChanged(it.lastIndex) }
                    }
                }
            }
        }
    }

    private fun tryAgainButton() {
        binding.btFollowersTryAgain.setOnClickListener {
            detailsViewModel.getUserFollowers(userName)
        }
    }

    private fun setUpRecyclerView(networkUtil: NetworkUtil) {
        userFollowingAdapter = ListUserAdapter(
            networkUtil = networkUtil,
            onItemClicked = {
                navigateToUserDetails(it)
            },
            onTryAgainClick = { detailsViewModel.getUserFollowers(userName) },
            isBookMark = false
        )
        binding.apply {
            followersRecycler.adapter = userFollowingAdapter
            followersRecycler.addOnScrollListener(this@FollowersFragment.scrollListener)
        }
    }

    private fun navigateToUserDetails(user: UserResponse) {
        findNavController()
            .navigate(FollowersFragmentDirections.formFollowersFragmentToUserDetails(
                user))
    }

    private fun navigateToBack() {
        binding.followersBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /*
* handle pagination
*  */
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val shouldPaginate = isAtLastItem && isNotAtBeginning && isScrolling

            if (shouldPaginate) {
                detailsViewModel.getUserFollowers(userName)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    /*
     *handle views visibility
     *  */
    private fun progressBarView(showView: Boolean) {
        binding.followersProgress.isVisible = showView
    }

    private fun noInternetView(showViews: Boolean) {
        binding.tvFollowersNoInternetConnection.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivFollowersServerError.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.btFollowersTryAgain.isVisible = showViews
    }
}