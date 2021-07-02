package com.example.githubdemo.users

import android.util.Log
import androidx.lifecycle.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.api.UserApiStatus
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserResponse
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepositoryImpl, private val networkUtil:NetworkUtil) :
    ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<UserApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<UserApiStatus>
        get() = _status

    private val _users = MutableLiveData<List<UserResponse>>()
    val users: LiveData<List<UserResponse>>
        get() = _users

    init {
        getAllUsers()
    }

    private fun getAllUsers() {
        viewModelScope.launch {
            if (networkUtil.hasInternetConnection()) {
                try {
                    _status.value = UserApiStatus.LOADING
                    val listResult = userRepository.getUsers()
                    if (listResult.isNotEmpty()) {
                        _users.value = listResult
                        _status.value = UserApiStatus.DONE
                    }
                } catch (t: Throwable) {
                    _status.value = UserApiStatus.ERROR
                    Log.e("userViewModel", "${t.message}")
                    _users.value = ArrayList()
                }
            } else {
                _status.value = UserApiStatus.NO_INTERNET_CONNECTION
            }
        }
    }
     fun swipeToRefresh(swipeRefresh: SwipeRefreshLayout) {
        swipeRefresh.setOnRefreshListener {
            _users.value = ArrayList()
            getAllUsers()
            swipeRefresh.isRefreshing = false
        }
    }
}