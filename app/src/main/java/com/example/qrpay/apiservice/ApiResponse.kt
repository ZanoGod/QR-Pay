package com.example.qrpay.apiservice

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@Keep
@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "pin") val pin: String
)

@Keep
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "token") val token: String,
    @Json(name = "message") val message: String,
    @Json(name = "role") val role: String? = null
)

@Keep
@JsonClass(generateAdapter = true)
data class BalanceResponse(
    @Json(name = "balance") val balance: Double? = null
)


@Keep
@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "phoneNumber") val phoneNumber: String? = null,
    @Json(name = "balance") val balance: Double? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "bankAccount") val bankAccount: BankAccountResponse? = null
)

@Keep
data class BankAccountResponse(
    @Json(name = "accountNumber") val accountNumber: String,
    @Json(name = "bankName") val bankName: String
)

@Keep
data class BankAccountReg(
    @Json(name = "accountNumber") val accountNumber: String,
    @Json(name = "bankName") val bankName: String
)

@Keep
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "name") val name: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "pin") val pin: String,
    @Json(name = "bankAccount") val bankAccount: BankAccountReg
)

@Keep
@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "message") val message: String,
    @Json(name = "token") val token: String
)

@Keep
@JsonClass(generateAdapter = true)
data class TopUpRequest(
    @Json(name = "userPhone") val userPhone: String,
    @Json(name = "amount") val amount: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class TopUpResponse(
    @Json(name = "message") val message: String,
    @Json(name = "referenceNumber") val referenceNumber: Int
)

@Keep
@JsonClass(generateAdapter = true)
data class PayRequest(
    @Json(name = "referenceNumber") val referenceNumber: String,
    @Json(name = "pin") val pin: String
)
@Keep
@JsonClass(generateAdapter = true)
data class PayResponse(
    @Json(name = "status") val status: String,
    @Json(name = "referenceNumber") val referenceNumber: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "merchantName")val merchantName: String,
    @Json(name = "merchantId")val merchantId: String,
    @Json(name = "message") val message: String
)

@Keep
@JsonClass(generateAdapter = true)
data class TransferRequest(
   @Json(name = "recipientPhone") val recipientPhone: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "pin") val pin: String
)

@Keep
@JsonClass(generateAdapter = true)
data class TransferResponse(
    @Json(name = "message") val message: String,
    @Json(name = "referenceNumber") val referenceNumber: String,
)
@Keep
@JsonClass(generateAdapter = true)
data class TransactionHistoryResponse(
    @Json(name = "_id") val _id: String,
    @Json(name = "type") val type: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "reference") val reference: String,
    @Json(name = "status") val status: String,
    @Json(name = "timestamp") val timestamp: String,
    @Json(name = "__v") val __v: Int,
    @Json(name = "displaySender") val displaySender: String,
    @Json(name = "displayRecipient") val displayRecipient: String,
)

