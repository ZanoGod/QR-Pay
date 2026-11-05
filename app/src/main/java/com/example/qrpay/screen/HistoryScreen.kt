package com.example.qrpay.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrpay.R
import com.example.qrpay.apiservice.TransactionHistoryResponse
import com.example.qrpay.ui.theme.Blue150
import com.example.qrpay.ui.theme.Blue200
import com.example.qrpay.ui.theme.Green800
import com.example.qrpay.ui.theme.Red700
import com.example.qrpay.view.TranHistoryViewModel
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    navController: NavController,
    viewModel: TranHistoryViewModel = hiltViewModel()
) {
    val transactionHistory = viewModel.transactionHistory
    val isLoadingState = viewModel.isLoading.collectAsState()
    val isLoading = isLoadingState.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blue200,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(text = "Transaction History", fontSize = 24.sp, color = Color.White)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Blue200,
                    )
                }
            } else if (transactionHistory.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transaction history found", textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn {
                    items(transactionHistory) { transaction ->
                        TransactionItems(transaction)
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItems(transaction: TransactionHistoryResponse) {

    val formattedTime = formatIsoDateTime(transaction.timestamp, "MMMM,dd yyyy HH:mm a")
    val transactionType = when (transaction.type) {
        "qr_payment" -> "QR Payment"
        "topup" -> "Top Up"
        "transfer" -> "Transfer"
        "qr_refund" -> "QR Refund"
        else -> transaction.type
    }
    val formattedAmount = formatAmount(transaction.amount)

    val formatIcon = when (transaction.type) {
        "qr_payment" -> R.drawable.qr_payment_trim
        "topup" -> R.drawable.topup_black
        "qr_refund" -> R.drawable.qr_refund
        else -> R.drawable.ic_send
    }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Blue150),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(id = formatIcon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))

            Column(
                horizontalAlignment = Alignment.Start, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = transactionType,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (transaction.type == "qr_payment") " - " else if (transaction.type == "transfer") " - " else " + ",
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.type == "qr_payment") Red700 else if (transaction.type == "transfer") Red700 else Green800,
                    )
                    Text(
                        text = "$formattedAmount MMK",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (transaction.type == "qr_payment") Red700 else if (transaction.type == "transfer") Red700 else Green800,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = transaction.displayRecipient,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = transaction.reference, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = formattedTime, style = MaterialTheme.typography.bodyMedium)


            }

//            Column(horizontalAlignment = Alignment.End) {
//                Text(
//                    text = "$formattedAmount MMK",
//                    fontSize = 18.sp,
//                    modifier = Modifier.wrapContentWidth(Alignment.End)
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = transaction.status,
//                    modifier = Modifier.wrapContentWidth(Alignment.End),
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = statusColor,
//                )
//            }
//            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatIsoDateTime(timestamp: String, pattern: String = "dd MMMM yyyy HH:mm:ss"): String {

    val offsetDateTimeYear = OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)

    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = offsetDateTimeYear.atZoneSameInstant(zoneId)

    val formatterHistory = DateTimeFormatter.ofPattern(pattern)

    return zonedDateTime.format(formatterHistory).replace("am", "AM").replace("pm", "PM")
}

