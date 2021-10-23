package com.example.githubdemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.githubdemo.R
import com.example.githubdemo.adapter.UserRepoAdapter
import com.example.githubdemo.databinding.FragmentStarredBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.detail.DetailsViewModel
import com.example.githubdemo.users.detail.DetailsViewModelProviderFactory
import com.example.githubdemo.utils.Constant
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.utils.Results


class StarredFragment : Fragment() {
    private var _binding: FragmentStarredBinding? = null
    private val binding get() = _binding!!
    private lateinit var repoAdapter: UserRepoAdapter
    val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    private val detailsViewModel: DetailsViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        DetailsViewModelProviderFactory(repository, networkUtil)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_starred, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        val userName = bundle?.getString(Constant.USER_NAME_KEY)
        if (userName != null) {
            detailsViewModel.getUserStarred(userName)
        }

        detailsViewModel.usersStarredStatus.observe(viewLifecycleOwner) { userRepo ->
            userRepo?.let { response ->
                when (response) {
                    is Results.Error -> {
                        progressbarView(false)
                    }
                    Results.Loading -> {
                        progressbarView(true)
                    }
                    Results.NoInternet -> {
                        progressbarView(false)
                    }
                    is Results.Success -> {
                        progressbarView(false)
                        repoAdapter.submitList(response.data)
                    }
                }
            }
        }
        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        repoAdapter = UserRepoAdapter(
            networkUtil = networkUtil,
            onTryAgainClick = {}
        )
        binding.starredRecycler.adapter = repoAdapter
    }

    private fun progressbarView(showViews: Boolean) {
        binding.starredProgress.isVisible = showViews
    }


}
