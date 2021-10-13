package com.example.githubdemo.users.detail.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.ListUserAdapter
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.databinding.FragmentFollowingBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.detail.DetailsViewModel
import com.example.githubdemo.users.detail.DetailsViewModelProviderFactory
import com.example.githubdemo.utils.Results


class FollowingFragment : Fragment() {
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!
    private lateinit var detailsViewModel: DetailsViewModel
    private lateinit var userFollowingAdapter: ListUserAdapter
    private var isScrolling = false
    private val args: FollowingFragmentArgs by navArgs()
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_following, container, false)
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

        userName = args.userName
        detailsViewModel.getUserFollowing(userName)
        detailsViewModel.getUserDetails(userName)


        userFollowingListResultsHandling()
        setUpRecyclerView(networkUtil)

    }

    private fun userFollowingListResultsHandling() {
        detailsViewModel.usersFollowingStatus.observe(viewLifecycleOwner) { userFollowing ->
            userFollowing?.let { response ->
                when (response) {
                    is Results.Error -> {
                        progressBarView(false)
                    }
                    Results.Loading -> {
                        progressBarView(true)
                    }
                    Results.NoInternet -> {
                        progressBarView(false)

                    }
                    is Results.Success -> {
                        progressBarView(false)
                        userFollowingAdapter.submitList(response.data)
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView(networkUtil: NetworkUtil) {
        userFollowingAdapter = ListUserAdapter(
            networkUtil = networkUtil,
            onItemClicked = {},
            onTryAgainClick = { detailsViewModel.getUserFollowing(userName) }
        )
        binding.apply {
            followingRecycler.adapter = userFollowingAdapter
            followingRecycler.addOnScrollListener(this@FollowingFragment.scrollListener)
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
                detailsViewModel.getUserFollowing(userName)
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


    private fun progressBarView(showView: Boolean) {
        binding.followingProgress.isVisible = showView
    }
}