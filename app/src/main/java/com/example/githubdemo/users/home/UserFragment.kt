package com.example.githubdemo.users.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.UserAdapter
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.databinding.FragmentUserBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.utils.Results
import com.google.android.material.snackbar.Snackbar


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var layoutManager: LinearLayoutManager
    var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = UserRepositoryImpl()
        val application = requireActivity().application
        val networkUtil = NetworkUtil(application)
        val viewModelFactory = UserViewModelProviderFactory(repository, networkUtil)
        userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)


        userViewModel.usersStatus.observe(viewLifecycleOwner, { usersResults ->
            usersResults?.let { response ->
                when (response) {
                    is Results.Success -> {
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        response.data?.let { userList ->
                            userAdapter.submitList(userList)
                            userAdapter.notifyItemChanged(userList.lastIndex)
                        }
                    }
                    Results.Loading -> {
                        progressbarView(true)
                        noInternetView(false)
                        serverErrorView(false)
                    }
                    is Results.Error -> {
                        userAdapter.submitList(emptyList())
                        progressbarView(false)
                        noInternetView(false)
                        serverErrorView(true)
                        response.message?.let { message ->
                            Log.e("UserFragment", "An error occured: $message")
                        }
                    }
                    Results.NoInternet -> {
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(true)
                    }
                }
            }
        })
        userViewModel.showSnackBar.observe(viewLifecycleOwner, { showSnackBar ->
            if (showSnackBar == true) {
                Snackbar.make(view, "NO INTERNET CONNECTION !!", Snackbar.LENGTH_SHORT).show()
            }
        })
        swipeRefresh()
        tryAgain()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            userViewModel = userViewModel,
            onItemClicked = { navigateToDetailsScreen() },
            onTryAgainClick = { userViewModel.getAllUsers() }
        )
        layoutManager = LinearLayoutManager(activity)
        binding.apply {
            userRecycler.layoutManager = layoutManager
            userRecycler.adapter = userAdapter
            userRecycler.addOnScrollListener(this@UserFragment.scrollListener)
        }

    }

    private fun navigateToDetailsScreen() {
        findNavController().navigate(R.id.userFragment_to_userDetails)
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
                userViewModel.getAllUsers()
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

    private fun swipeRefresh() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            userViewModel.swipeToRefresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun tryAgain() {
        binding.btTryAgain.setOnClickListener {
            userViewModel.getAllUsers()
        }
    }

    /*
    *handle views visibility
    *  */
    private fun noInternetView(showViews: Boolean) {
        binding.tvNoInternet.isVisible = showViews
        binding.btTryAgain.isVisible = showViews
    }

    private fun progressbarView(showViews: Boolean) {
        binding.userProgressBar.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivServerError.isVisible = showViews
        binding.btTryAgain.isVisible = showViews
    }
}