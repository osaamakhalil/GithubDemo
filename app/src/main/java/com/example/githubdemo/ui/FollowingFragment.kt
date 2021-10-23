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
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.databinding.FragmentFollowingBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.detail.DetailsViewModel
import com.example.githubdemo.users.detail.DetailsViewModelProviderFactory
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Results


class FollowingFragment : Fragment() {
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!
    private val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    private val detailsViewModel: DetailsViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        DetailsViewModelProviderFactory(repository, networkUtil)
    }
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

        userName = args.userName
        detailsViewModel.getUserFollowing(userName)
        detailsViewModel.getUserDetails(userName)


        userFollowingListResultsHandling()
        setUpRecyclerView(networkUtil)
        tryAgainButton()
        navigateToBack()
    }

    private fun userFollowingListResultsHandling() {
        detailsViewModel.usersFollowingStatus.observe(viewLifecycleOwner) { userFollowing ->
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
        binding.btFollowingTryAgain.setOnClickListener {
            detailsViewModel.getUserFollowing(userName)
        }
    }

    private fun navigateToBack() {
        binding.followingBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpRecyclerView(networkUtil: NetworkUtil) {
        userFollowingAdapter = ListUserAdapter(
            networkUtil = networkUtil,
            onItemClicked = {
                navigateToUserDetails(it)
            },
            onTryAgainClick = { detailsViewModel.getUserFollowing(userName) }
            ,isBookMark = false
        )
        binding.apply {
            followingRecycler.adapter = userFollowingAdapter
            followingRecycler.addOnScrollListener(this@FollowingFragment.scrollListener)
        }
    }

    private fun navigateToUserDetails(user: UserResponse) {
        findNavController()
            .navigate(FollowingFragmentDirections.formFollowingFragmentToUserDetails(
                user))
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

    /*
     *handle views visibility
      *  */
    private fun progressBarView(showView: Boolean) {
        binding.followingProgress.isVisible = showView
    }

    private fun noInternetView(showViews: Boolean) {
        binding.tvFollowingNoInternetConnection.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivFollowingServerError.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.btFollowingTryAgain.isVisible = showViews
    }
}