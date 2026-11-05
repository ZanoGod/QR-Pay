package com.example.qrpay.view

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrpay.apiservice.TransactionHistoryResponse
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranHistoryViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _transactionHistory = mutableStateOf<List<TransactionHistoryResponse>>(emptyList())
    val transactionHistory: List<TransactionHistoryResponse> get() = _transactionHistory.value

//    private val _isLoading = mutableStateOf(false)
//    val isLoading: Boolean get() = _isLoading.value

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        fetchTransactionHistory()
    }

    fun fetchTransactionHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val transactionHistoryResult = apiRepository.getTranHistory()
                transactionHistoryResult.onSuccess { transactions ->
                    _transactionHistory.value = transactions
                    Log.d("TranHistoryViewModel", "Transaction history fetched successfully ${transactions[1]}")
                }.onFailure { error ->
                    Log.e("TranHistoryViewModel", "Failed to fetch transaction history: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("TranHistoryViewModel", "Unexpected error while fetching transaction history: ${e.message}")
            } finally {
                delay(400)
                _isLoading.value = false
            }
        }
    }
}
