package com.example.githubdemo.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.R
import com.example.githubdemo.adapter.UserAdapter
import com.example.githubdemo.api.UserApiStatus
import com.example.githubdemo.databinding.FragmentUserBinding
import com.example.githubdemo.repository.UserRepositoryImpl


class UserFragment : Fragment() {

    lateinit var binding: FragmentUserBinding
    lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        setupRecyclerView()

        val repository = UserRepositoryImpl()
        val viewModelFactory = UserViewModelProviderFactory(repository)
        val userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)

        userViewModel.users.observe(viewLifecycleOwner, Observer { usersList ->
            usersList.let {
                userAdapter.submitList(usersList)
            }
        })
        //for status
        userViewModel.status.observe(viewLifecycleOwner, Observer { status ->
            setStatus(binding, status)
        })


        return binding.root
    }


    private fun setupRecyclerView() {
        userAdapter = UserAdapter()
        binding.apply {
            userRecycler.adapter = userAdapter

        }
    }

    private fun setStatus(binding: FragmentUserBinding, status: UserApiStatus) {
        binding.apply {
            status?.let {
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
                    }
                }
            }
        }
    }

}