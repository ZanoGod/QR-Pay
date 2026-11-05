package com.example.qrpay.repo

import android.util.Log
import com.example.qrpay.apiservice.ApiService
import com.example.qrpay.apiservice.BankAccountReg
import com.example.qrpay.apiservice.LoginRequest
import com.example.qrpay.apiservice.LoginResponse
import com.example.qrpay.apiservice.PayRequest
import com.example.qrpay.apiservice.PayResponse
import com.example.qrpay.apiservice.RegisterRequest
import com.example.qrpay.apiservice.RegisterResponse
import com.example.qrpay.apiservice.TopUpRequest
import com.example.qrpay.apiservice.TopUpResponse
import com.example.qrpay.apiservice.TransactionHistoryResponse
import com.example.qrpay.apiservice.TransferRequest
import com.example.qrpay.apiservice.TransferResponse
import com.example.qrpay.apiservice.UserProfileResponse
import com.example.qrpay.db.DataLoginManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ApiRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataLoginManager: DataLoginManager
) {

    internal suspend fun getToken(): String = dataLoginManager.getAuthToken().firstOrNull() ?: ""

    suspend fun login(phoneNumber: String, pin: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(phoneNumber, pin)
                val response = apiService.userLogin(request)

                response.body()?.let {
                    return@withContext Result.success(it)
                } ?: run {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    return@withContext Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun checkTokenValidity(token: String): Boolean {
        return try {
            val response = apiService.getUserProfile("Bearer $token")
            response.isSuccessful
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("ApiRepository", "Token validation failed: ${e.message}", e)
            false
        }
    }


    suspend fun register(name: String, phoneNumber: String, pin: String, accountNumber: String, bankName: String): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(name, phoneNumber, pin, BankAccountReg(accountNumber, bankName))
                val response = apiService.registerUser(request)
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        Result.success(registerResponse)
                    } else {
                        Log.e("ApiRepository", "Response: ${response.raw()}")
                        response.errorBody()?.let {
                            Log.e("ApiRepository", "Error Body: ${it.string()}")
                        }
                        Log.e("ApiRepository", "Empty response body")
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: IOException) {
                Log.e("ApiRepository", "Network error: ${e.message}")
                Result.failure(e)
            } catch (e: HttpException) {
                Log.e("ApiRepository", "HTTP error: ${e.message}")
                Result.failure(e)
            } catch (e: Exception) {
                Log.e("ApiRepository", "Unexpected error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun getUserProfile(): Result<UserProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken()
                if (token.isNotEmpty()) {
                    val response = apiService.getUserProfile("Bearer $token")
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Authentication token not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    }

    suspend fun getTranHistory(): Result<List<TransactionHistoryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken()
                if (token.isNotEmpty()) {
                    val response = apiService.getTransactionHistory("Bearer $token")
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Authentication token not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }




    suspend fun topUp(userPhone: String, amount: Double): Result<TopUpResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val topUpRequest = TopUpRequest(userPhone, amount)
                val response =
                    apiService.topUp("Bearer ${dataLoginManager.getAuthToken()}", topUpRequest)
                if (response.isSuccessful) {

                    val topUpResponse = response.body()

                    if (topUpResponse != null) {
                        Result.success(topUpResponse)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(HttpException(response))
                }
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: HttpException) {
                Result.failure(e)
            }
        }
    }


    suspend fun Pay(referenceNumber: String, pin: String): Result<PayResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val PayReq = PayRequest(referenceNumber, pin)
                val authToken = dataLoginManager.getAuthToken().firstOrNull() ?: ""
                Log.d("ApiRepository", "Auth Token: $authToken")
                val response =
                    apiService.pay("Bearer $authToken", PayReq)
                if (response.isSuccessful) {
                    Log.e("PayViewModel", "Pay successful: $response")
                    val payResponse = response.body()
                    if (payResponse != null) {
                        Result.success(payResponse)
                    } else {
                        Log.e("ApiRepo", "Pay failed: $response")
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                   // Result.failure(HttpException(response))

                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: HttpException) {
                Result.failure(e)
            }
        }
    }


    suspend fun transfer(recipientPhone: String, amount: Double, pin: String): Result<TransferResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val transferRequest = TransferRequest(recipientPhone,amount, pin)
                val authToken = dataLoginManager.getAuthToken().firstOrNull() ?: ""
                val response =
                    apiService.transfer("Bearer $authToken", transferRequest)
                if (response.isSuccessful) {
                    val transferResponse = response.body()

                    if (transferResponse != null) {
                        Result.success(transferResponse)
                    } else {
                        Log.e("ApiRepo", "Pay failed: $response")
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                  //  Result.failure(HttpException(response))
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: HttpException) {
                Result.failure(e)
            }
        }
    }
}

fun parseErrorMessage(errorBody: String?): String {
    // If the error body is in JSON format and contains a "message" field, extract it
    return try {
        val jsonObject = JSONObject(errorBody)
        jsonObject.optString("message", "Unknown error")  // Return the "message" field or a default message
    } catch (e: Exception) {
        "Unknown error"
    }
}