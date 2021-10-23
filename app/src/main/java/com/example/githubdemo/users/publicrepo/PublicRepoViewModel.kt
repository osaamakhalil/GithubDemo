package com.example.githubdemo.users.publicrepo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.UserRepo
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PublicRepoViewModel(
    private val userRepository: UserRepositoryImpl,
    private val networkUtil: NetworkUtil,
) : ViewModel() {

    private var repoPage = 1
    private var totalCount = 0

    private val _reposStatus = MutableLiveData<Results<List<UserRepo>>>()
    val reposStatus: LiveData<Results<List<UserRepo>>>
        get() = _reposStatus

    private var reposList: MutableList<UserRepo>? = null

    init {
        getPublicRepos()
    }

    fun getPublicRepos() {
        if (totalCount != reposList?.size) {
            networkUtil.isLastPage(false)
            if (networkUtil.hasInternetConnection()) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        if (reposList == null) _reposStatus.postValue(Results.Loading)
                        val repoResults = userRepository.getPublicRepos(repoPage, "N")
                        val searchList = repoResults.userRepo
                        if (searchList.isNotEmpty()) {
                            if (reposList == null) {
                                reposList = searchList.toMutableList()
                            } else {
                                reposList?.addAll(searchList)
                            }
                            _reposStatus.postValue(Results.Success(reposList))
                            totalCount = repoResults.totalCount
                            repoPage++
                        }
                    } catch (t: Throwable) {
                        _reposStatus.postValue(Results.Error(t.message))
                        Log.e("UserViewModel", "${t.message}")
                    }
                }
            } else {
                //if no list to show, then show some icon on center of screen
                if (reposList == null) {
                    _reposStatus.postValue(Results.NoInternet)
                } else {
                    //if list not empty, used that to show no internet on bottom of page
                    _reposStatus.postValue(Results.Success(reposList))
                }
            }
        } else {
            networkUtil.isLastPage(true)
            Log.e("PublicRepoViewModel", "these is the last page")
        }
    }
}