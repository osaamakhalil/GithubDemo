package com.example.githubdemo.users.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserDetails
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.users.model.UserResponse
import com.example.githubdemo.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil,
) : ViewModel(
) {
    private var followPages = 1
    private var numberOfFollowing = 0
    var numberOfFollower = 0

    private val _usersDetailsStatus = MutableLiveData<Results<UserDetails>>()
    val usersDetailsStatus: LiveData<Results<UserDetails>>
        get() = _usersDetailsStatus

    private val _usersRepoStatus = MutableLiveData<Results<List<UserRepo>>>()
    val usersRepoStatus: LiveData<Results<List<UserRepo>>>
        get() = _usersRepoStatus

    private val _usersFollowingStatus = MutableLiveData<Results<List<UserResponse>>>()
    val usersFollowingStatus: LiveData<Results<List<UserResponse>>>
        get() = _usersFollowingStatus

    private val _usersFollowersStatus = MutableLiveData<Results<List<UserResponse>>>()
    val usersFollowersStatus: LiveData<Results<List<UserResponse>>>
        get() = _usersFollowersStatus

    //add a new results to this list and use it to send to the view to present it
    private var userPresenterList: MutableList<UserResponse>? = null


    //make this request to get number of followers/following
    fun getUserDetails(name: String) {
        if (networkUtil.hasInternetConnection()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _usersDetailsStatus.postValue(Results.Loading)
                    val followCount = userRepository.getUsersDetails(name)
                    _usersDetailsStatus.postValue(Results.Success(followCount))
                    numberOfFollowing = followCount.following
                    numberOfFollower = followCount.followers
                } catch (t: Throwable) {
                    Log.e("DetailsViewModel", "${t.message}")
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
                    val userRepos = userRepository.getUserRepo(name,50)
                    if (userRepos.isNotEmpty()) {
                        _usersRepoStatus.postValue(Results.Success(userRepos))
                    }
                } catch (t: Throwable) {
                    Log.e("DetailsViewModel", "${t.message}")
                    _usersRepoStatus.postValue(Results.Error(t.message))
                }
            }
        } else {
            _usersRepoStatus.postValue(Results.NoInternet)
        }
    }

    //to get list of following
    fun getUserFollowing(userName: String) {
        //check if in the last page
        if (numberOfFollowing != userPresenterList?.size) {
            networkUtil.isLastPage(false)
            if (networkUtil.hasInternetConnection()) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        if (userPresenterList == null) _usersFollowingStatus.postValue(Results.Loading)
                        val newUserFollowing =
                            userRepository.getUserFollowing(userName, followPages)
                        if (newUserFollowing.isNotEmpty()) {
                            if (userPresenterList == null) {
                                userPresenterList = newUserFollowing
                            } else {
                                userPresenterList?.addAll(newUserFollowing)
                            }
                            _usersFollowingStatus.postValue(Results.Success(userPresenterList))
                            followPages++
                        } else {
                            _usersFollowingStatus.postValue(Results.Success(emptyList()))
                        }
                    } catch (t: Throwable) {
                        Log.e("DetailsViewModel", "${t.message}")
                        _usersFollowingStatus.postValue(Results.Error(t.message))
                    }
                }
            } else {
                _usersFollowingStatus.postValue(Results.NoInternet)
            }
        } else {
            networkUtil.isLastPage(true)
            Log.e("DetailsViewModel", "these is the last page")
        }
    }

    //to get list of followers
    fun getUserFollowers(userName: String) {
        //check if in the last page
        if (numberOfFollower != userPresenterList?.size) {
            networkUtil.isLastPage(false)
            if (networkUtil.hasInternetConnection()) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        if (userPresenterList == null) _usersFollowersStatus.postValue(Results.Loading)
                        val newUserFollowers =
                            userRepository.getUserFollowers(userName, followPages)
                        if (newUserFollowers.isNotEmpty()) {
                            if (userPresenterList == null) {
                                userPresenterList = newUserFollowers
                            } else {
                                userPresenterList?.addAll(newUserFollowers)
                            }
                            _usersFollowersStatus.postValue(Results.Success(userPresenterList))
                            followPages++
                        } else {
                            _usersFollowersStatus.postValue(Results.Success(emptyList()))
                        }
                    } catch (t: Throwable) {
                        Log.e("DetailsViewModel", "${t.message}")
                        _usersFollowersStatus.postValue(Results.Error(t.message))
                    }
                }
            } else {
                _usersFollowersStatus.postValue(Results.NoInternet)
            }
        } else {
            networkUtil.isLastPage(true)
            Log.e("DetailsViewModel", "these is the last page")
        }
    }


}