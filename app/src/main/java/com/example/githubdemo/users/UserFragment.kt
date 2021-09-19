package com.example.githubdemo.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.UserAdapter
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.api.UserApiStatus
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

    var noInternet = false

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


        userViewModel.users.observe(viewLifecycleOwner, { usersResults ->
            usersResults?.let { response ->
                when (response) {
                    is Results.Success -> {
                        hideProgress()
                        hideServerError()
                        hideNoInternet()
                        response.data?.let { userList ->
                            userAdapter.submitList(userList)
                        }
                    }
                    is Results.Loading -> {
                        showProgress()
                        hideNoInternet()
                        hideServerError()
                    }
                    is Results.Error -> {
                        hideProgress()
                        hideNoInternet()
                        showServerError()
                        userAdapter.submitList(emptyList())
                        response.message?.let { message ->
                            Log.e("UserFragment", "An error occured: $message")
                        }
                    }
                }
            }
        })
        // for internetStatus
//        userViewModel.internetStatus.observe(viewLifecycleOwner, { status ->
//            if (status == UserApiStatus.NO_INTERNET_CONNECTION) {
//                if (userAdapter.itemCount == 0) {
//                    hideProgress()
//                    hideServerError()
//                    showNoInternet()
//                    noInternet = true
//                } else {
//                    Snackbar.make(view, "NO INTERNET CONNECTION !!", Snackbar.LENGTH_SHORT).show()
//                    noInternet = true
//                }
//            } else {
//                noInternet = false
//            }
//        })

        userViewModel.hasInternet.observe(viewLifecycleOwner, { hasInternetConnection ->
            if (hasInternetConnection) {
                noInternet = false
            } else if (userAdapter.itemCount == 0) {
                hideProgress()
                hideServerError()
                showNoInternet()
                noInternet = true
            } else {
                Snackbar.make(view, "NO INTERNET CONNECTION !!", Snackbar.LENGTH_SHORT).show()
                noInternet = true
            }

        })
        swipeRefresh()
        tryAgain()
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        userAdapter = UserAdapter(this)
        layoutManager = LinearLayoutManager(activity)
        binding.apply {
            userRecycler.layoutManager = layoutManager
            userRecycler.adapter = userAdapter
            userRecycler.addOnScrollListener(this@UserFragment.scrollListener)
        }

    }

    var isLoading = false
    var isScrolling = false
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoading = !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val shouldPaginate = isNotLoading && isAtLastItem && isNotAtBeginning && isScrolling
            if (shouldPaginate) {
                userViewModel.needMoreUsers()
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
            userViewModel.getAllUsers()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun tryAgain() {
        binding.btTryAgain.setOnClickListener {
            userViewModel.getAllUsers()
        }
    }

    private fun hideNoInternet() {
        binding.tvNoInternet.visibility = View.GONE
        binding.btTryAgain.visibility = View.GONE
    }

    private fun showNoInternet() {
        binding.tvNoInternet.visibility = View.VISIBLE
        binding.btTryAgain.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.userProgressBar.visibility = View.GONE
    }

    private fun showProgress() {
        binding.userProgressBar.visibility = View.VISIBLE
    }

    private fun hideServerError() {
        binding.ivServerError.visibility = View.GONE
    }

    private fun showServerError() {
        binding.ivServerError.visibility = View.VISIBLE
    }

}