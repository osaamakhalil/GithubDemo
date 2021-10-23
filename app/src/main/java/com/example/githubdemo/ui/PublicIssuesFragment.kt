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
import com.example.githubdemo.adapter.IssuesAdapter
import com.example.githubdemo.databinding.FragmentPublicIssuesBinding
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.issues.PublicIssuesViewModel
import com.example.githubdemo.users.issues.PublicIssuesViewModelProviderFactory
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.utils.Results


class PublicIssuesFragment : Fragment() {
    private var _binding: FragmentPublicIssuesBinding? = null
    private val binding get() = _binding!!

    val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    val publicIssuesViewModel: PublicIssuesViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        PublicIssuesViewModelProviderFactory(networkUtil, repository)
    }
    private lateinit var issuesAdapter: IssuesAdapter
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_public_issues, container, false)
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_public_issues, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        handleIssuesListResponse()
        navigateBack()
        tryAgain()
    }


    private fun handleIssuesListResponse() {
        publicIssuesViewModel.issueStatus.observe(viewLifecycleOwner) { repoResults ->
            repoResults?.let { response ->
                when (response) {
                    is Results.Success -> {
                        tryAgainView(false)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        response.data?.let { repoList ->
                            issuesAdapter.submitList(repoList)
                            issuesAdapter.notifyItemChanged(repoList.lastIndex)
                        }
                    }
                    Results.Loading -> {
                        tryAgainView(false)
                        progressbarView(true)
                        noInternetView(false)
                        serverErrorView(false)
                    }
                    is Results.Error -> {
                        issuesAdapter.submitList(emptyList())
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
                publicIssuesViewModel.getPublicIssue()
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

    private fun setupRecyclerView() {
        issuesAdapter = IssuesAdapter(networkUtil = networkUtil,
            onTryAgainClick = { publicIssuesViewModel.getPublicIssue() }
        )
        binding.apply {
            issueRecycler.adapter = issuesAdapter
            issueRecycler.addOnScrollListener(this@PublicIssuesFragment.scrollListener)
        }
    }

    private fun navigateBack() {
        binding.issueBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun tryAgain() {
        binding.issuesBtTryAgain.setOnClickListener {
            publicIssuesViewModel.getPublicIssue()
        }
    }

    /*
      *handle views visibility
      *  */
    private fun noInternetView(showViews: Boolean) {
        binding.issuesTvNoInternet.isVisible = showViews
    }

    private fun progressbarView(showViews: Boolean) {
        binding.issueProgressBar.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivServerErrorIssues.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.issuesBtTryAgain.isVisible = showViews
    }

}