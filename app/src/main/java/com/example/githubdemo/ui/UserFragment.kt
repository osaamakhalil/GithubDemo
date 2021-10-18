package com.example.githubdemo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.ListUserAdapter
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.databinding.FragmentUserBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.home.UserViewModel
import com.example.githubdemo.users.home.UserViewModelProviderFactory
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Results
import com.google.android.material.snackbar.Snackbar


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var listUserAdapter: ListUserAdapter
    val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }

    private val userViewModel: UserViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        UserViewModelProviderFactory(repository, networkUtil)
    }
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userListResultsHandling()
        snackBarView(view)
        setupRecyclerView(networkUtil)
        swipeRefresh()
        tryAgainButton()
        navigateToBack()
    }

    private fun userListResultsHandling() {
        userViewModel.usersStatus.observe(viewLifecycleOwner, { usersResults ->
            usersResults?.let { response ->
                when (response) {
                    is Results.Success -> {
                        tryAgainView(false)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        response.data?.let { userList ->
                            listUserAdapter.submitList(userList)
                            listUserAdapter.notifyItemChanged(userList.lastIndex)
                        }
                    }
                    Results.Loading -> {
                        tryAgainView(false)
                        progressbarView(true)
                        noInternetView(false)
                        serverErrorView(false)
                    }
                    is Results.Error -> {
                        listUserAdapter.submitList(emptyList())
                        tryAgainView(true)
                        progressbarView(false)
                        noInternetView(false)
                        serverErrorView(true)
                        response.message?.let { message ->
                            Log.e("UserFragment", "An error occured: $message")
                        }
                    }
                    Results.NoInternet -> {
                        tryAgainView(true)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(true)
                    }
                }
            }
        })
    }

    private fun snackBarView(view: View) {
        userViewModel.showSnackBar.observe(viewLifecycleOwner, { showSnackBar ->
            if (showSnackBar == true) {
                Snackbar.make(view, "NO INTERNET CONNECTION !!", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(networkUtil: NetworkUtil) {
        listUserAdapter = ListUserAdapter(
            networkUtil = networkUtil,
            onItemClicked =
            {
                navigateToDetailsScreen(it)
            },
            onTryAgainClick = { userViewModel.getUsers() },
            isBookMark = false
        )
        binding.apply {
            userRecycler.adapter = listUserAdapter
            userRecycler.addOnScrollListener(this@UserFragment.scrollListener)
        }

    }

    private fun navigateToDetailsScreen(user: UserResponse) {
        findNavController().navigate(UserFragmentDirections.toUserDetails(user))
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
                userViewModel.getUsers()
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

    private fun navigateToBack() {
        binding.userBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun swipeRefresh() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            userViewModel.swipeToRefresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun tryAgainButton() {
        binding.btTryAgain.setOnClickListener {
            userViewModel.getUsers()
        }
    }

    /*
    *handle views visibility
    *  */
    private fun noInternetView(showViews: Boolean) {
        binding.tvNoInternet.isVisible = showViews
    }

    private fun progressbarView(showViews: Boolean) {
        binding.userProgressBar.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivServerError.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.btTryAgain.isVisible = showViews
    }
}