package com.example.githubdemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.githubdemo.R
import com.example.githubdemo.adapter.ListUserAdapter
import com.example.githubdemo.bookmark.BookMarkViewModel
import com.example.githubdemo.bookmark.BookMarkViewModelFactory
import com.example.githubdemo.databinding.FragmentBookMarkBinding
import com.example.githubdemo.db.UsersDatabase
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.NetworkUtil


class BookMarkFragment : Fragment() {
    private var _binding: FragmentBookMarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var listUserAdapter: ListUserAdapter
    private val networkUtil: NetworkUtil by lazy {
        NetworkUtil(requireContext())
    }
    private val bookMarkViewModel: BookMarkViewModel by viewModels {
        val repository =
            UserRepositoryImpl(UsersDatabase.getInstance(requireActivity().application))
        BookMarkViewModelFactory(repository, networkUtil)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_book_mark, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookMarkViewModel.getAllUsers().observe(viewLifecycleOwner) {
            listUserAdapter.submitList(it)
        }
        setupRecyclerView(networkUtil)
        navigateBack()
    }

    private fun setupRecyclerView(networkUtil: NetworkUtil) {
        listUserAdapter = ListUserAdapter(
            networkUtil = networkUtil,
            onItemClicked =
            {
                navigateToUserDetails(it)
            },
            onTryAgainClick = { },
            isBookMark = true
        )
        binding.apply {
            bookmarkRecycler.adapter = listUserAdapter
        }
    }

    private fun navigateToUserDetails(user: UserResponse) {
        findNavController().navigate(BookMarkFragmentDirections.fromBookMarkFragmentToUserDetails(
            user))
    }

    private fun navigateBack() {
        binding.bookMarkBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}