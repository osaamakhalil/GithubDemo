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
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.databinding.FragmentRepositoryBinding
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.users.detail.DetailsViewModel
import com.example.githubdemo.users.detail.DetailsViewModelProviderFactory
import com.example.githubdemo.utils.Results
import com.example.githubdemo.utils.Constant.Companion.USER_NAME_KEY

class RepositoryFragment() : Fragment() {
    private var _binding: FragmentRepositoryBinding? = null
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repository, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bundle = this.arguments
        val userName = bundle?.getString(USER_NAME_KEY)
        if (userName != null) {
            detailsViewModel.getUserRepo(userName)
        }



        detailsViewModel.usersRepoStatus.observe(viewLifecycleOwner) { userRepo ->
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
        repoAdapter = UserRepoAdapter()
        binding.repoRecycler.adapter = repoAdapter
    }

    private fun progressbarView(showViews: Boolean) {
        binding.repoProgress.isVisible = showViews
    }

}