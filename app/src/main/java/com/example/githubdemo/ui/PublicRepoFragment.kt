package com.example.githubdemo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.R
import com.example.githubdemo.adapter.UserRepoAdapter
import com.example.githubdemo.databinding.FragmentPublicRepoBinding
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.publicrepo.PublicRepoViewModel
import com.example.githubdemo.users.publicrepo.PublicRepoViewModelProviderFactory
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.utils.Results


class PublicRepoFragment : Fragment() {
    private var _binding: FragmentPublicRepoBinding? = null
    private val binding get() = _binding!!
    private lateinit var repoAdapter: UserRepoAdapter

    private val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    private val publicRepoViewModel: PublicRepoViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        PublicRepoViewModelProviderFactory(repository, networkUtil)
    }
    private var isScrolling = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_public_repo, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleReposResponse()
        setupRecyclerView()
        navigateBack()
        tryAgain()
    }


    private fun handleReposResponse() {
        publicRepoViewModel.reposStatus.observe(viewLifecycleOwner) { repoResults ->
            repoResults?.let { response ->
                when (response) {
                    is Results.Success -> {
                        tryAgainView(false)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        response.data?.let { repoList ->
                            repoAdapter.submitList(repoList)
                            repoAdapter.notifyItemChanged(repoList.lastIndex)
                        }
                    }
                    Results.Loading -> {
                        tryAgainView(false)
                        progressbarView(true)
                        noInternetView(false)
                        serverErrorView(false)
                    }
                    is Results.Error -> {
                        repoAdapter.submitList(emptyList())
                        tryAgainView(true)
                        progressbarView(false)
                        noInternetView(false)
                        serverErrorView(true)
                        response.message?.let { message ->
                            Log.e("PublicFragment", "An error occurred: $message")
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
        }
    }


    private fun setupRecyclerView() {
        repoAdapter = UserRepoAdapter(
            networkUtil = networkUtil,
            onTryAgainClick = { publicRepoViewModel.getPublicRepos() }
        )
        binding.apply {
            userRepoRecycler.adapter = repoAdapter
            userRepoRecycler.addOnScrollListener(this@PublicRepoFragment.scrollListener)
        }
    }

    private fun navigateBack() {
        binding.reposBackArrow.setOnClickListener {
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
                publicRepoViewModel.getPublicRepos()
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

    private fun tryAgain() {
        binding.repoBtTryAgain.setOnClickListener {
            publicRepoViewModel.getPublicRepos()
        }
    }

    /*
     *handle views visibility
     *  */
    private fun noInternetView(showViews: Boolean) {
        binding.repoTvNoInternet.isVisible = showViews
    }

    private fun progressbarView(showViews: Boolean) {
        binding.repoProgressBar.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivServerErrorRepo.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.repoBtTryAgain.isVisible = showViews
    }
}

