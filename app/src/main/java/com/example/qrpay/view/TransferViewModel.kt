package com.example.qrpay.view

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransferViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading: MutableState<Boolean> get() = _isLoading

    private val _transferResult = mutableStateOf<Result<String>?>(null)
    val transferResult: MutableState<Result<String>?> get() = _transferResult

    private val _showSuccessAnimation = mutableStateOf(false)
    val showSuccessAnimation: MutableState<Boolean> get() = _showSuccessAnimation

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: MutableState<String> get() = _phoneNumber

    private val _pin = mutableStateOf("")
    val pin: MutableState<String> get() = _pin

    private val _amount = mutableStateOf("")
    val amount: MutableState<String> get() = _amount

    private val _message = mutableStateOf<String?>(null)
    val message: MutableState<String?> get() = _message

    fun transferAmount() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val amountDouble = _amount.value.toDoubleOrNull()

                val transResult = apiRepository.transfer(
                    recipientPhone = _phoneNumber.value,
                    amount = amountDouble ?: 0.0,
                    pin = _pin.value
                )
                if (transResult.isSuccess) {
                    val transferResponse = transResult.getOrNull()
                    _message.value = transferResponse?.message
                    phoneNumber.value = transferResponse?.referenceNumber.toString()
                    _transferResult.value =
                        Result.success(transferResponse?.referenceNumber.toString())

                    _showSuccessAnimation.value = true
                } else {
                    val error = transResult.exceptionOrNull()
                    _transferResult.value = Result.failure(error ?: Exception("Unknown error"))
                    Log.e("TransferViewModel", "Transfer failed: ${error?.message}")
                }
            } catch (e: Exception) {
                _transferResult.value = Result.failure(e)
                Log.e("TransferViewModel", "Unexpected error: ${e.message}")
            } finally {
                _isLoading.value = false

            }
        }
    }
}
