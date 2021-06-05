package com.example.githubdemo.users

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.githubdemo.adapter.UserAdapter
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.api.UserApiStatus
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserResponse
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepositoryImpl, val app: Application) :
    AndroidViewModel(app) {

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

    fun getAllUsers() {
        viewModelScope.launch {
            if (NetworkUtil.hasInternetConnection(app)) {
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

    fun swipeToRefresh(swipeRefresh: SwipeRefreshLayout, userAdapter: UserAdapter) {
        swipeRefresh.setOnRefreshListener {
            userAdapter.submitList(ArrayList())
            getAllUsers()
            swipeRefresh.isRefreshing = false
        }
    }
}