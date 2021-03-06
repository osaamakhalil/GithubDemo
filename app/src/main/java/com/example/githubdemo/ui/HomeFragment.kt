package com.example.githubdemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentHomeBinding
import android.content.Intent
import android.net.Uri


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateToUsersFragment()
        navigateToReposFragment()
        navigateToIssuesFragment()
        openGithubWepPage()
    }


    private fun navigateToUsersFragment() {
        binding.usersCardView.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.fromHomeFragmentToUserFragment())
        }
    }

    private fun navigateToReposFragment() {
        binding.repoCardView.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.fromHomeFragmentToPublicRepoFragment())
        }
    }

    private fun navigateToIssuesFragment(){
        binding.issuesCardView.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.fromHomeFragmentToPublicIssuesFragment())
        }
    }

    private fun openGithubWepPage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
        binding.gitHubWebCardView.setOnClickListener {
            startActivity(browserIntent)
        }
    }

}