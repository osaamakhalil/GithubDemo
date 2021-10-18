package com.example.githubdemo.users.home

import android.util.Log
import androidx.lifecycle.*
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Results
import com.example.githubdemo.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil
) : ViewModel() {
    //for paging handles
    private var since = 0

    private val _usersStatus = MutableLiveData<Results<List<UserResponse>>>()
    val usersStatus: LiveData<Results<List<UserResponse>>>
        get() = _usersStatus

    //add a new results to this list and use it to send to the view to present it
    private var userResponse: MutableList<UserResponse>? = null

    //use single live event to use it to show snack bar
    private val _showSnackBar = SingleLiveEvent<Boolean>()
    val showSnackBar: SingleLiveEvent<Boolean>
        get() = _showSnackBar

    init {
        getUsers()
    }

    /*
    * make call to get all user list
    * */
    fun getUsers() {
        if (networkUtil.hasInternetConnection()) {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (userResponse == null) _usersStatus.postValue(Results.Loading)
                    val newList = userRepository.getUsers(since)
                    if (newList.isNotEmpty()) {
                        if (userResponse == null) {
                            userResponse = newList.toMutableList()
                        } else {
                            userResponse?.addAll(newList)
                        }
                        _usersStatus.postValue(
                            Results.Success(
                                userResponse,
                            )
                        )
                        val lastUser = newList.last()
                        since = lastUser.id
                    }
                } catch (t: Throwable) {
                    Log.e("userViewModel", "${t.message}")
                    _usersStatus.postValue(Results.Error(t.message, emptyList()))
                }
            }
        } else {
            //if no list to show, then show some icon on center of screen
            if (userResponse == null) {
                _usersStatus.postValue(Results.NoInternet)
            } else {
                //if list not empty, used that to show no internet on bottom of page
                _usersStatus.postValue(
                    Results.Success(
                        userResponse
                    )
                )
            }
        }
    }

    /*
    * use this fun when swipe screen to refresh and make call to get the first page
    * with the id "since" = 0
    *  */
    fun swipeToRefresh() {
        if (networkUtil.hasInternetConnection()) {
            since = 0
            userResponse = mutableListOf()
            getUsers()
            _showSnackBar.postValue(false)
        } else {
            _showSnackBar.postValue(true)
        }
    }
}