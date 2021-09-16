package com.example.githubdemo.users

import android.util.Log
import androidx.lifecycle.*
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.api.UserApiStatus
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil
) : ViewModel() {
    //for paging handles
    private var since = 0

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _internetStatus = MutableLiveData<UserApiStatus>()

    // The external immutable LiveData for the request status String
    val internetStatus: LiveData<UserApiStatus>
        get() = _internetStatus

    private val _users = MutableLiveData<Results<List<UserResponse>>>()
    val users: LiveData<Results<List<UserResponse>>>
        get() = _users
    var userResponse: MutableList<UserResponse>? = null

    init {
        getAllUsers()
    }

    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            if (networkUtil.hasInternetConnection()) {
                try {
                    if (userResponse == null) _users.postValue(Results.Loading())
                    val listResult = userRepository.getUsers(since)
                    if (listResult.isNotEmpty()) {
                        if (userResponse == null) {
                            userResponse = listResult.toMutableList()
                        } else {
                            val oldUserList = userResponse
                            oldUserList?.addAll(listResult)
                        }
                        _users.postValue(Results.Success(userResponse))
                        val lastUser = listResult.last()
                        since = lastUser.id
                    }
                } catch (t: Throwable) {
                    Log.e("userViewModel", "${t.message}")
                    _users.postValue(Results.Error(t.message, emptyList()))
                }
            } else {
                _internetStatus.postValue(UserApiStatus.NO_INTERNET_CONNECTION)
            }
        }
    }

    fun needMoreUsers() {
        getAllUsers()
    }
}