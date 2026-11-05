package com.example.qrpay.apiservice

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun userLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>


    @GET("wallet/balance")
    suspend fun getBalance(@Header("Authorization") authToken: String): Response<BalanceResponse>

    @GET("auth/profile")
    suspend fun getUserProfile(@Header("Authorization") authToken: String): Response<UserProfileResponse>


    @Headers("Content-Type: application/json")
    @POST("auth/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<RegisterResponse>


    @Headers("Content-Type: application/json")
    @POST("wallet/topup")
    suspend fun topUp(@Header("Authorization") authToken: String, @Body topUpRequest: TopUpRequest): Response<TopUpResponse>

    @Headers("Content-Type: application/json")
    @POST("qr/pay")
    suspend fun pay(@Header("Authorization") authToken: String, @Body payRequest: PayRequest): Response<PayResponse>

//    @GET("auth/profile")
//    suspend fun  checkTokenValidity(@Header("Authorization") authToken: String): Response<TokenValidationResponse>


//    @GET("wallet/transaction")
//    suspend fun getTransactionHistory(@Header("Authorization") authToken: String, @Query("page") page: Int, @Query("limit") limit: Int): Response<TransactionHistoryResponse>

    @GET("wallet/transactions")
    suspend fun getTransactionHistory(
        @Header("Authorization") authToken: String
    ): Response<List<TransactionHistoryResponse>>

    @Headers("Content-Type: application/json")
    @POST("wallet/transfer")
    suspend fun transfer(@Header("Authorization") authToken: String, @Body transferRequest: TransferRequest): Response<TransferResponse>

}
