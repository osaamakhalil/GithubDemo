package com.example.githubdemo.ui

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.ListUserAdapter
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.databinding.FragmentUserBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.Users.UserViewModel
import com.example.githubdemo.users.Users.UserViewModelProviderFactory
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Results
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.view.inputmethod.InputMethodManager


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var listUserAdapter: ListUserAdapter
    val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    var searchText = ""
    var isSearchList = false
    private lateinit var cashUsersList: List<UserResponse>

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

        userListResults()
        snackBarView(view)
        setupRecyclerView(networkUtil)
        swipeRefresh()
        tryAgainButton()
        navigateToBack()
        searchForUser()
        userSearchListResults()
    }

    private fun userListResults() {
        userViewModel.usersStatus.observe(viewLifecycleOwner, { usersResults ->
            usersResults?.let { response ->
                when (response) {
                    is Results.Success -> {
                        tryAgainView(false)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        response.data?.let { userList ->
                            isSearchList = false
                            cashUsersList = userList
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

    private fun userSearchListResults() {
        userViewModel.searchStatus.observe(viewLifecycleOwner, { usersResults ->
            usersResults?.let { response ->
                when (response) {
                    is Results.Success -> {
                        tryAgainView(false)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        response.data?.let { userList ->
                            isSearchList = true
                            listUserAdapter.submitList(response.data)
                            listUserAdapter.notifyItemChanged(response.data.lastIndex)
                        }
                    }
                    Results.Loading -> {
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

    private fun searchForUser() {
        binding.userSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                var debouncePeriod: Long = 500
                private val coroutineScope = lifecycle.coroutineScope
                private var searchJob: Job? = null

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        newText?.let {
                            delay(debouncePeriod)
                            if (it.isEmpty()) {
                                listUserAdapter.submitList(cashUsersList)
                                isSearchList = false
                                closeKeyboard()
                            } else {
                                userViewModel.clearSearch()
                                userViewModel.searchForUsers(it)
                                searchText = it
                            }
                        }
                    }
                    return false
                }
            }
        )
    }

     private fun closeKeyboard() {
        val view: View? = requireActivity().currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
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
        closeKeyboard()
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
                if (isSearchList) {
                    userViewModel.searchForUsers(searchText)
                    isScrolling = false
                } else {
                    userViewModel.getUsers()
                    isScrolling = false
                }
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