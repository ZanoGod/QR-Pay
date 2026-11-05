package com.example.qrpay.view

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrpay.apiservice.PayResponse
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class PayViewModel @Inject constructor(
    private val apiRepository: ApiRepository
): ViewModel(){

    private val _isLoading = mutableStateOf(false)
    val isLoading: MutableState<Boolean> get() = _isLoading

    private val _payResult = mutableStateOf<Result<PayResponse>?>(null)
    val payResult: MutableState<Result<PayResponse>?> get() = _payResult

    private val _paySuccess = mutableStateOf<PayResponse?>(null)
    val paySuccess: MutableState<PayResponse?> get() = _paySuccess

    private val _showSuccessAnimation = mutableStateOf(false)
    val showSuccessAnimation: MutableState<Boolean> get() = _showSuccessAnimation

    private val _referenceNumber = mutableStateOf("")
    val referenceNumber: MutableState<String> get() = _referenceNumber

    private val _pin = mutableStateOf("")
    val pin: MutableState<String> get() = _pin

    private val _message = mutableStateOf<String?>(null)
    val message: MutableState<String?> get() = _message

    private val _amount = mutableStateOf("")
    val amount: MutableState<String> get() = _amount

    private val _merchantName = mutableStateOf("")
    val merchantName: MutableState<String> get() = _merchantName

    private val _merchantId = mutableStateOf("")
    val merchantId: MutableState<String> get() = _merchantId


    fun pay() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                if (_referenceNumber.value.isEmpty()) {
                    _payResult.value = Result.failure(Exception("Reference number cannot be empty"))
                    _message.value = "Reference number is required"
                    return@launch
                }
                val payResult = apiRepository.Pay(
                    _referenceNumber.value,
                    _pin.value
                )
                _payResult.value = payResult

                if (payResult.isSuccess) {
                    val payResponse = payResult.getOrNull()

                    _message.value = payResponse?.status ?: "N/A"
                    _amount.value = payResponse?.amount?.toString() ?: "N/A"
                    _merchantName.value = payResponse?.merchantName ?: "N/A"
                    _merchantId.value = payResponse?.merchantId ?: "N/A"
                    _referenceNumber.value = payResponse?.referenceNumber ?: "N/A"
                    _showSuccessAnimation.value = true
                    _paySuccess.value = payResponse

                    Log.d("PayViewModel", "Pay successful: $payResponse")
                    Log.d("PayViewModel", "Pay successful: ${_message.value}")
                    Log.d("PayViewModel", "Pay successful: ${_amount.value}")
                    Log.d("PayViewModel", "Pay successful: ${_merchantName.value}")
                    Log.d("PayViewModel", "Pay successful: ${_merchantId.value}")

                } else {
//                    _message.value = "Payment failed: ${payResult.exceptionOrNull()?.message}"
//                    Log.e("PayViewModel", "Pay failed: ${payResult.exceptionOrNull()?.message}")

                    val errorMessage = payResult.exceptionOrNull()?.message ?: "Payment failed"
                    _message.value = errorMessage
                    Log.e("PayViewModel", "Pay failed: $errorMessage")
                }

            } catch (e: Exception) {
                _message.value = "Unexpected error: ${e.message}"
                Log.e("PayViewModel", "Unexpected error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}

