package com.example.qrpay.view

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrpay.apiservice.UserProfileResponse
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserProfileViewModel @Inject constructor(
private val apiRepository: ApiRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile

    private val _isRefresh = MutableStateFlow(false)
    val isRefresh: StateFlow<Boolean> = _isRefresh.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUserProfile()
    }
//    private var isFetched = false

    fun fetchUserProfile() {
//        if (isFetched) return
//        isFetched = true

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isRefresh.value = true
                val userProfileResult = apiRepository.getUserProfile()
                userProfileResult.onSuccess { userProfileResponse ->
                    Log.d("UserProfileViewModel", "User profile fetched successfully: $userProfileResponse")
                    _userProfile.value = userProfileResponse
                }.onFailure { error ->
                    Log.e("UserProfileViewModel", "Failed to fetch user profile: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Unexpected error while fetching user profile: ${e.message}")
            } finally {
                delay(400)
                _isLoading.value = false
                _isRefresh.value = false
            }
        }
    }

}
