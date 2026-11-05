package com.example.qrpay.view

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading: MutableState<Boolean> get() = _isLoading

    private val _registerResult = mutableStateOf<Result<String>?>(null)
    val registerResult: MutableState<Result<String>?> get() = _registerResult

    private val _showSuccessAnimation = mutableStateOf(false)
    val showSuccessAnimation: MutableState<Boolean> get() = _showSuccessAnimation

    private val _name = mutableStateOf("")
    val name: MutableState<String> get() = _name

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: MutableState<String> get() = _phoneNumber

    private val _pin = mutableStateOf("")
    val pin: MutableState<String> get() = _pin

    private val _accountNumber = mutableStateOf("")
    val accountNumber: MutableState<String> get() = _accountNumber

    private val _bankName = mutableStateOf("")
    val bankName: MutableState<String> get() = _bankName

    private val _token = mutableStateOf<String?>(null)
    val token: MutableState<String?> get() = _token

    private val _message = mutableStateOf<String?>(null)
    val message: MutableState<String?> get() = _message


    fun registerUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _registerResult.value = null

                val result = apiRepository.register(
                    name = _name.value,
                    phoneNumber = _phoneNumber.value,
                    pin = _pin.value,
                    accountNumber = _accountNumber.value,
                    bankName = _bankName.value
                )

                if (result.isSuccess) {
                    val registerResponse = result.getOrNull()
                    _token.value = registerResponse?.token
                    _message.value = registerResponse?.message
                    _registerResult.value = Result.success("Registration successful!")
                    _showSuccessAnimation.value = true
                } else {
                    val error = result.exceptionOrNull()
                    Log.e("RegisterViewModel", "Registration failed: ${error?.message}")
                    _registerResult.value = Result.failure(error ?: Exception("Unknown error"))
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Unexpected error: ${e.message}")
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}