package com.example.qrpay.view

import android.util.Log
import androidx.activity.result.launch
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrpay.apiservice.TopUpResponse
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TopUpViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: MutableState<Boolean> get() = _isLoading

    private val _topUpResult = mutableStateOf<Result<TopUpResponse>?>(null)
    val topUpResult: MutableState<Result<TopUpResponse>?> get() = _topUpResult

    private val _token = mutableStateOf<String?>(null)
    val token: MutableState<String?> get() = _token

    private val _showSuccessAnimation = mutableStateOf(false)
    val showSuccessAnimation: MutableState<Boolean> get() = _showSuccessAnimation

    private val _showFailureAnimation = mutableStateOf(false)
    val showFailureAnimation: MutableState<Boolean> get() = _showFailureAnimation

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: MutableState<String> get() = _phoneNumber

    private val _message = mutableStateOf<String?>(null)
    val message: MutableState<String?> get() = _message

    private val _referenceNumber = mutableStateOf("")
    val referenceNumber: MutableState<String> get() = _referenceNumber

    private val _amount = mutableStateOf("")
    val amount: MutableState<String> get() = _amount

    fun topUpAmount() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _topUpResult.value = null

                val amountDouble = _amount.value.toDoubleOrNull()

                if (amountDouble == null) {
                    _topUpResult.value = Result.failure(Exception("Invalid amount"))
                    return@launch
                }

                val topUpResult = apiRepository.topUp(
                    _phoneNumber.value,
                    amountDouble
                )

                if (topUpResult.isSuccess) {
                    val topUpResponse = topUpResult.getOrNull()
                    _message.value = topUpResponse?.message
                    _referenceNumber.value = topUpResponse?.referenceNumber.toString()

                    _topUpResult.value = Result.success(topUpResponse!!)
                    _showSuccessAnimation.value = true
                } else {
                    _showFailureAnimation.value = true
                    val error = topUpResult.exceptionOrNull()
                    Log.e("TopUpViewModel", "Top up failed: ${error?.message}")
                    _topUpResult.value = Result.failure(error ?: Exception("Unknown error"))
                }
            } catch (e: Exception) {
                Log.e("TopUpViewModel", "Unexpected error: ${e.message}")
                _topUpResult.value = Result.failure(e)
                _showFailureAnimation.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }


}