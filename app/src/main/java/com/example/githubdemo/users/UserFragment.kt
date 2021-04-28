package com.example.githubdemo.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentUserBinding


class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentUserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user,container,false)
        
        return binding.root
    }

}