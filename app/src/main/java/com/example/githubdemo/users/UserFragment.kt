package com.example.githubdemo.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.UserAdapter
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.api.UserApiStatus
import com.example.githubdemo.databinding.FragmentUserBinding
import com.example.githubdemo.repository.UserRepositoryImpl


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private var loading = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)

        layoutManager = LinearLayoutManager(activity)
        userAdapter = UserAdapter()
        binding.apply {
            userRecycler.layoutManager = layoutManager
            userRecycler.adapter = userAdapter
        }

        val repository = UserRepositoryImpl()
        val application = requireActivity().application
        val networkUtil = NetworkUtil(application)
        val viewModelFactory = UserViewModelProviderFactory(repository, networkUtil)
        userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)


        userViewModel.users.observe(viewLifecycleOwner, Observer { usersList ->
            usersList?.let {
                userAdapter.submitList(usersList)
            }
        })
        //for status
        userViewModel.status.observe(viewLifecycleOwner, Observer { status ->
            setStatus(binding, status)
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh()
        tryAgain()
        handlePages()
    }

    var previousTotal = 0
    val visibleThreshold = 10
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0

    private fun handlePages() {
        //handle pages
        binding.userRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false
                            previousTotal = totalItemCount
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        userViewModel.needMoreUsers()
                        loading = true
                    }

                }
            }
        })
    }

    private fun swipeRefresh() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            userAdapter.submitList(emptyList())
            userViewModel.getAllUsers()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun tryAgain() {
        binding.btTryAgain.setOnClickListener {
            userViewModel.getAllUsers()
        }
    }

    private fun setStatus(binding: FragmentUserBinding, status: UserApiStatus) {
        binding.apply {
            status.let {
                when (status) {
                    UserApiStatus.LOADING -> {
                        userProgressBar.visibility = View.VISIBLE
                        ivStatus.visibility = View.GONE
                    }
                    UserApiStatus.ERROR -> {
                        ivStatus.visibility = View.VISIBLE
                        userProgressBar.visibility = View.GONE
                        ivStatus.setImageResource(R.drawable.ic_connection_error)
                    }
                    UserApiStatus.DONE -> {
                        userProgressBar.visibility = View.GONE
                        ivStatus.visibility = View.GONE
                        tvNoInternet.visibility = View.GONE
                        btTryAgain.visibility = View.GONE
                    }
                    UserApiStatus.NO_INTERNET_CONNECTION -> {
                        userProgressBar.visibility = View.GONE
                        tvNoInternet.visibility = View.VISIBLE
                        btTryAgain.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}