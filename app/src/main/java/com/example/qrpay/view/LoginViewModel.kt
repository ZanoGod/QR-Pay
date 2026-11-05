package com.example.qrpay.view

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.qrpay.db.DataLoginManager
import com.example.qrpay.apiservice.LoginResponse
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
        private val apiRepository: ApiRepository,
    val dataLogin: DataLoginManager,
//    private val userProfileViewModel: UserProfileViewModel
) : ViewModel() {

    private val userProfileViewModel = UserProfileViewModel(apiRepository)
    private val _balance = mutableStateOf<Double?>(null)
    val balance: MutableState<Double?> get() = _balance

    private val _loginResult = mutableStateOf<Result<String>?>(null)
    val loginResult: MutableState<Result<String>?> get() = _loginResult

    private val _isLoading = mutableStateOf(false)
    val isLoading: MutableState<Boolean> get() = _isLoading

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: MutableState<String> get() = _phoneNumber

    private val _pin = mutableStateOf("")
    val pin: MutableState<String> get() = _pin

    private val _loginMessage = mutableStateOf<Result<String>?>(null)
    val loginMessage: MutableState<Result<String>?> get() = _loginMessage

    private val _role = mutableStateOf<String?>(null)
    val role = _role

    private val _token = mutableStateOf<String?>(null)
    val token: MutableState<String?> get() = _token

    init {
        viewModelScope.launch {
            val currentToken = dataLogin.getAuthToken().firstOrNull()
            _token.value = currentToken
            if (currentToken != null) {
                if (isValidToken(currentToken)) {
                    userProfileViewModel.fetchUserProfile()
                } else {
                    dataLogin.clearAuthToken()
                }
            }
        }
    }

    fun updatePhoneNumber(newNumber: String) {
        _phoneNumber.value = newNumber
    }

    fun updatePin(newPin: String) {
        _pin.value = newPin
    }

    fun login() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _loginResult.value = null

                val loginResult: Result<LoginResponse> =
                    apiRepository.login(_phoneNumber.value, _pin.value)

                loginResult.onSuccess { loginResponse ->
                    Log.d("LoginViewModel", "Login successful: $loginResponse")
                    _token.value = loginResponse.token
                    _role.value = loginResponse.role
                    Log.d("LoginResult", "Role : ${_role.value} ")
                    dataLogin.saveAuthToken(
                        _token.value!!,
                        _role.value.toString(),
                    )
                    _loginResult.value = Result.success(_token.value!!)
                }.onFailure { error ->
                    Log.e("LoginViewModel", "Login failed: ${error.message}")
                    _loginResult.value = Result.failure(error)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error: ${e.message}")
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    suspend fun isValidToken(currentToken: String?): Boolean {
        if (currentToken == null) return false
        return try {
            val response = apiRepository.checkTokenValidity(currentToken)
            response
        } catch (e: Exception) {
            false
        }
    }

}