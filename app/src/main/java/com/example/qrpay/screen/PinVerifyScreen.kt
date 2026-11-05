package com.example.qrpay.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrpay.NavRoute
import com.example.qrpay.ui.theme.Blue200
import com.example.qrpay.view.PayViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinVerifyScreen(navController: NavController, ref: String, amount: String) {
    var pin by remember { mutableStateOf("") }

    val payViewModel: PayViewModel = hiltViewModel()
    val payResult by payViewModel.payResult
    val message by payViewModel.message

    var topSnackBarMessage by remember { mutableStateOf("") }
    var showTopSnackBar by remember { mutableStateOf(false) }

    // Back navigation
    BackHandler { navController.popBackStack(NavRoute.QR_CODE_SCANNER, inclusive = false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blue200,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(text = "Enter PIN", fontSize = 24.sp, color = Color.White)
                },
            )
        },
        snackbarHost = {
            Box(
                modifier = Modifier.fillMaxSize().padding(top = 30.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                AnimatedVisibility(
                    modifier = Modifier.padding(top = 80.dp),
                    visible = showTopSnackBar,
                    enter = slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { -it }
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(500),
                        targetOffsetY = { -it }
                    )
                ) {
                    Snackbar(
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
                        containerColor = Blue200,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(
                            text = topSnackBarMessage,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(text = "Reference number:", color = Color.DarkGray)
                    Text(text = ref, color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Amount: ",
                        fontSize = 20.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = amount,
                        fontSize = 20.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "MMK", fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 13.dp)
                        .height(66.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(15.dp))
                        .shadow(15.dp, RoundedCornerShape(15.dp), clip = true)
                        .background(Color.White, RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (pin.isBlank()) {
                        Text(
                            text = "Enter PIN",
                            style = TextStyle(
                                fontSize = 28.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 66.sp,
                                color = Color.Gray.copy(0.6f)
                            )
                        )
                    } else {
                        Text(
                            text = " * ".repeat(pin.length), //mask with pin
                            style = TextStyle(
                                fontSize = 28.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 66.sp,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                NumericKeypad(
                    onNumberClick = { number ->
                        if (pin.length < 4) {
                            pin += number
                        }
                    },
                    onClearClick = {
                        if (pin.isNotEmpty()) {
                            pin = pin.dropLast(1)
                        }
                    },
                    onConfirmClick = {
                        if(pin.isBlank()){
                            topSnackBarMessage = "Pin is empty!! \n Please enter your PIN."
                            showTopSnackBar = true
                            return@NumericKeypad
                        }
                        payViewModel.referenceNumber.value = ref
                        payViewModel.pin.value = pin
                        payViewModel.pay()
                    },
                    modifier = Modifier.weight(1f)
                )
                LaunchedEffect(showTopSnackBar) {
                    if (showTopSnackBar) {
                        delay(2200L)
                        showTopSnackBar = false
                    }
                }
                LaunchedEffect(payResult) {
                    payResult?.fold(
                        onSuccess = {
                            val merchantName = payViewModel.merchantName.value
                            navController.navigate(NavRoute.SUCCESS + "/$ref/$amount/$merchantName")
                        },
                        onFailure = {
                          topSnackBarMessage = "Payment failed: ${it.message}"
                            showTopSnackBar = true
                        }
                    )
                }

                // show success or not
//                payResult?.let { result ->
//                    when {
//                        result.isSuccess -> {
//                            val merchantName = payViewModel.merchantName.value
//                            navController.navigate(NavRoute.SUCCESS+ "/$ref/$amount/$merchantName")
//                        }
//                        result.isFailure -> {
//                            val errMsg = message ?: "Payment Failed!"
//                            topSnackBarMessage = "Payment Failed! \n $errMsg"
//                            showTopSnackBar = true
//                            delay(2000)
//                            showTopSnackBar = false
//                            Toast.makeText(LocalContext.current, errMsg, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }

            }
        }

    }
}
