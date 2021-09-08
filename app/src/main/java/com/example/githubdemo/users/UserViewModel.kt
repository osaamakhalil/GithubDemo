package com.example.githubdemo.users

import android.util.Log
import androidx.lifecycle.*
import com.example.githubdemo.api.NetworkUtil
import com.example.githubdemo.api.UserApiStatus
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil
) :
    ViewModel() {
    private var userPerPage = 10

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
        viewModelScope.launch(Dispatchers.IO) {
            if (networkUtil.hasInternetConnection()) {
                try {
                    _status.postValue(UserApiStatus.LOADING)
                    val listResult = userRepository.getUsers(userPerPage)
                    if (listResult.isNotEmpty()) {
                        _users.postValue(listResult)
                        _status.postValue(UserApiStatus.DONE)
                    }
                } catch (t: Throwable) {
                    _status.postValue(UserApiStatus.ERROR)
                    Log.e("userViewModel", "${t.message}")

                    _users.postValue(emptyList())
                }
            } else {
                _status.postValue(UserApiStatus.NO_INTERNET_CONNECTION)
            }
        }
    }

    fun needMoreUsers() {
        if (userPerPage != 100) {
            Log.e("viewModelPages", " -> $userPerPage")
            userPerPage += 10
            getAllUsers()
            _status.postValue(UserApiStatus.PAGE_LOADING)
        }
    }
}