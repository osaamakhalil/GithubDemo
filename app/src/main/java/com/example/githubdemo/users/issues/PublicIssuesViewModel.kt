package com.example.githubdemo.users.issues

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.repository.UserRepositoryImpl
import com.example.githubdemo.users.model.IssuesItem
import com.example.githubdemo.users.model.PublicIssues
import com.example.githubdemo.utils.NetworkUtil
import com.example.githubdemo.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PublicIssuesViewModel(
    private val networkUtil: NetworkUtil,
    private val userRepository: UserRepositoryImpl,
) : ViewModel() {

    private var issuePage = 1

    private val _issueStatus = MutableLiveData<Results<List<IssuesItem>>>()
    val issueStatus: LiveData<Results<List<IssuesItem>>>
        get() = _issueStatus

    private var issueListPresenters: MutableList<IssuesItem>? = null

    init {
        getPublicIssue()
    }

    fun getPublicIssue() {
        if (networkUtil.hasInternetConnection()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (issueListPresenters == null) _issueStatus.postValue(Results.Loading)
                    val issueResults = userRepository.getPublicIssues(issuePage, "N")
                    val issueResultsList = issueResults.issuesItems
                    if (issueResultsList.isNotEmpty()) {
                        if (issueListPresenters == null) {
                            issueListPresenters = issueResultsList.toMutableList()
                        } else {
                            issueListPresenters?.addAll(issueResultsList)
                        }
                        _issueStatus.postValue(Results.Success(issueListPresenters))
                        issuePage++
                    }
                } catch (t: Throwable) {
                    _issueStatus.postValue(Results.Error(t.message))
                    Log.e("UserViewModel", "${t.message}")
                }
            }
        } else {
            //if no list to show, then show some icon on center of screen
            if (issueListPresenters == null) {
                _issueStatus.postValue(Results.NoInternet)
            } else {
                //if list not empty, used that to show no internet on bottom of page
                _issueStatus.postValue(Results.Success(issueListPresenters))
            }
        }
    }
}