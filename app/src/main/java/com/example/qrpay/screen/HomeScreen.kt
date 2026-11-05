package com.example.qrpay.screen

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.qrpay.NavRoute
import com.example.qrpay.R
import com.example.qrpay.apiservice.TransactionHistoryResponse
import com.example.qrpay.ui.theme.Blue150
import com.example.qrpay.ui.theme.Blue500
import com.example.qrpay.ui.theme.Blue700
import com.example.qrpay.ui.theme.Green700
import com.example.qrpay.view.LoginViewModel
import com.example.qrpay.view.TranHistoryViewModel
import com.example.qrpay.view.UserProfileViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {

    val activity = (LocalContext.current as? Activity)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    //transactions
    val viewModel: TranHistoryViewModel = hiltViewModel()
    val transactionHistory = viewModel.transactionHistory
    val isLoading by viewModel.isLoading.collectAsState()

    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val isRefresh by userProfileViewModel.isRefresh.collectAsState()
      val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefresh || isLoading)
  //  val swipeRefreshState = rememberPullToRefreshState()


    val balance = userProfile?.balance
    val accountNo = userProfile?.bankAccount?.accountNumber
    val bankName = userProfile?.bankAccount?.bankName

    val formattedAmount = formatAmount(balance ?: 0.0)

    BackHandler {
        if (backPressedOnce) {
            activity?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press again to exit!", Toast.LENGTH_SHORT).show()
            scope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }

    fun refresh() {
        scope.launch {
            userProfileViewModel.fetchUserProfile()
            viewModel.fetchTransactionHistory()
        }
    }

    Scaffold(
        containerColor = colorResource(R.color.primary),
        bottomBar = {
            BottomNavigationBar(navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { refresh() },
                indicator = { state, trigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = trigger,
                        contentColor = Color.White,
                        backgroundColor = Blue500
                    )
                }
            )
            {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, end = 12.dp, start = 12.dp, bottom = 0.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(22.dp))
                            .clickable {
                                navController.navigate(NavRoute.PROFILE)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(id = R.drawable.user_profile),
                                alignment = Alignment.Center,
                                contentDescription = "User profile",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    "Welcome back,",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "${userProfile?.name} (Role: ${userProfile?.role})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Phone: ${userProfile?.phoneNumber}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("My Balance", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = " $formattedAmount MMK",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BankInfo("Bank Name:", bankName)
                        Spacer(modifier = Modifier.height(4.dp))
                        BankInfo("Account Number:", accountNo)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ActionButton(icon = R.drawable.ic_send, text = "Send", onClick = {
                                navController.navigate(NavRoute.TRANSFER)
                            })
                            ActionButton(icon = R.drawable.ic_add_card,
                                text = "Top Up",
                                isVisible = userProfile?.role != "user",
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Top Up button clicked",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            ActionButton(
                                icon = R.drawable.ic_history,
                                text = "History",
                                onClick = {
                                    navController.navigate(NavRoute.HISTORY)
                                })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        HorizontalDivider(
            color = Color.Gray, thickness = 1.dp, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
        ) {
            Text(
                "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .weight(1f)
        ) {
            if (transactionHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transaction history found", textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(transactionHistory) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItem(
    transaction: TransactionHistoryResponse,
) {
    val formattedTime = formatIsoTimestamp(transaction.timestamp)
    val transactionType = when (transaction.type) {
        "qr_payment" -> "QR Payment"
        "topup" -> "Top Up"
        "transfer" -> "Transfer"
        "qr_refund" -> "QR Refund"
        else -> transaction.type
    }
    val formattedAmount = formatAmount(transaction.amount)
    val statusColor = if (transaction.status == "success") Green700 else Color.Black

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
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                painter = painterResource(id = formatIcon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(25.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = transactionType, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = formattedTime, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$formattedAmount MMK",
                    fontSize = 18.sp,
                    modifier = Modifier.wrapContentWidth(Alignment.End)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.status,
                    modifier = Modifier.wrapContentWidth(Alignment.End),
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun BankInfo(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value ?: "",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}

@Composable
fun ActionButton(icon: Int, text: String, onClick: () -> Unit, isVisible: Boolean = true) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(if (isVisible) 1f else 0.3f)
    ) {
        Box(modifier = Modifier
            .clip(CircleShape)
            .background(Blue700, CircleShape)
            .clickable { if (isVisible) onClick() }) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 0.dp, vertical = 0.dp)
            .background(
                colorResource(R.color.bd_primary),
                shape = RoundedCornerShape(25.dp)
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = Icons.Outlined.Person,
            contentDescription = "Home",
            onClick = {
                navController.navigate(NavRoute.PROFILE)
            },
            label = "Profile"
        )
        ScannerButton(navController)
        BottomNavItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Profile",
            onClick = {
                loginViewModel.viewModelScope.launch {
                    loginViewModel.dataLogin.clearAuthToken()
                    navController.navigate(NavRoute.LOGIN)
                }
            }, label = "logout"
        )
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(24.dp)
                .scale(scaleX = -1f, scaleY = 1f),
            tint = Color.Black
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ScannerButton(navController: NavController) {
    Box(modifier = Modifier
        .size(67.dp)
        .clip(CircleShape)
        .clickable {
            navController.navigate(NavRoute.QR_CODE_SCANNER)
        }
        .background(colorResource(R.color.ic_launcher_background)),
        contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.qr_code_scanner),
            contentDescription = "Scan QR Code",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.Black)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatIsoTimestamp(timestamp: String, pattern: String = "dd MMMM yyyy HH:mm:ss"): String {
    // Parse the input timestamp
    val offsetDateTime = OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)

    // Get the device's default time zone
    val zoneId = ZoneId.systemDefault()

    // Convert to local time zone
    val zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId)

    // Format the time output
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return zonedDateTime.format(formatter).replace("am", "AM").replace("pm", "PM")
}


fun formatAmount(amount: Double): String {
    val decimalFormat = DecimalFormat("#,###.#")
    val formattedValue = decimalFormat.format(amount)

    return if (formattedValue.endsWith(".0") || formattedValue.endsWith(",")) {
        formattedValue.substring(0, formattedValue.length - 2)
    } else if (formattedValue.endsWith(".00") || formattedValue.endsWith(",00")) {
        formattedValue.substring(0, formattedValue.length - 3)
    } else {
        formattedValue
    }
}