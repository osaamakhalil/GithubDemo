package com.example.githubdemo.users.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil
) : ViewModel(
) {

    private val _usersDetailsStatus = MutableLiveData<Results<UserDetails>>()
    val usersDetailsStatus: LiveData<Results<UserDetails>>
        get() = _usersDetailsStatus

    private val _usersRepoStatus = MutableLiveData<Results<List<UserRepo>>>()
    val usersRepoStatus: LiveData<Results<List<UserRepo>>>
        get() = _usersRepoStatus



    fun getUserDetails(name: String) {
        if (networkUtil.hasInternetConnection()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _usersDetailsStatus.postValue(Results.Loading)
                    val followCount = userRepository.getUsersDetails(name)
                    _usersDetailsStatus.postValue(Results.Success(followCount))
                } catch (t: Throwable) {
                    Log.e("userViewModel", "${t.message}")
                    _usersDetailsStatus.postValue(Results.Error(t.message))
                }
            }
        } else {
            _usersDetailsStatus.postValue(Results.NoInternet)
        }
    }

    fun getUserRepo(name: String) {
        if (networkUtil.hasInternetConnection()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _usersRepoStatus.postValue(Results.Loading)
                    val userRepos = userRepository.getUserRepo(name)
                    if (userRepos.isNotEmpty()) {
                        _usersRepoStatus.postValue(Results.Success(userRepos))
                    }
                } catch (t: Throwable) {
                    Log.e("userViewModel", "${t.message}")
                    _usersRepoStatus.postValue(Results.Error(t.message))
                }
            }
        } else {
            _usersRepoStatus.postValue(Results.NoInternet)
        }
    }


}