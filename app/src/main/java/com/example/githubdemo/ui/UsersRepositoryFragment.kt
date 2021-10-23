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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.UserRepoAdapter
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.databinding.FragmentRepositoryBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.detail.DetailsViewModel
import com.example.githubdemo.users.detail.DetailsViewModelProviderFactory
import com.example.githubdemo.utils.Results
import com.example.githubdemo.utils.Constant.Companion.USER_NAME_KEY

class UsersRepositoryFragment() : Fragment() {
    private var _binding: FragmentRepositoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var repoAdapter: UserRepoAdapter
    val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    private lateinit var usersName: String
    private var isScrolling = false

    private val detailsViewModel: DetailsViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        DetailsViewModelProviderFactory(repository, networkUtil)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repository, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bundle = this.arguments
        val userName = bundle?.getString(USER_NAME_KEY)
        if (userName != null) {
            usersName = userName
        }
        detailsViewModel.getUserRepo(usersName)
        detailsViewModel.getUserDetails(usersName)

        handleUsersRepoList()
        setUpRecyclerView()
        tryAgain()
    }

    private fun handleUsersRepoList() {
        detailsViewModel.usersRepoStatus.observe(viewLifecycleOwner) { userRepo ->
            userRepo?.let { response ->
                when (response) {
                    is Results.Error -> {
                        tryAgainView(true)
                        progressbarView(false)
                        noInternetView(false)
                        serverErrorView(true)
                    }
                    Results.Loading -> {
                        tryAgainView(false)
                        noInternetView(false)
                        serverErrorView(false)
                        progressbarView(true)
                    }
                    Results.NoInternet -> {
                        progressbarView(false)
                        tryAgainView(true)
                        serverErrorView(false)
                        noInternetView(true)
                    }
                    is Results.Success -> {
                        tryAgainView(false)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        repoAdapter.submitList(response.data)
                        response.data?.let { repoAdapter.notifyItemChanged(it.lastIndex) }
                    }
                }
            }
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
                detailsViewModel.getUserRepo(usersName)
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

    private fun tryAgain(){
        binding.userRepoBtTryAgain.setOnClickListener {
            detailsViewModel.getUserRepo(usersName)
            detailsViewModel.getUserDetails(usersName)
        }
    }
    private fun setUpRecyclerView() {
        repoAdapter = UserRepoAdapter(
            networkUtil = networkUtil,
            onTryAgainClick = { detailsViewModel.getUserRepo(usersName) }
        )
        binding.apply {
            repoRecycler.adapter = repoAdapter
            repoRecycler.addOnScrollListener(this@UsersRepositoryFragment.scrollListener)
        }
    }

    /*
     *handle views visibility
     *  */
    private fun noInternetView(showViews: Boolean) {
        binding.userRepoTvNoInternet.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.userIvServerErrorRepo.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.userRepoBtTryAgain.isVisible = showViews
    }

    private fun progressbarView(showViews: Boolean) {
        binding.userRepoProgress.isVisible = showViews
    }

}