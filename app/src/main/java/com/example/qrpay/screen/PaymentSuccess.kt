package com.example.qrpay.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrpay.NavRoute
import com.example.qrpay.R
import com.example.qrpay.ui.theme.Blue200
import com.example.qrpay.ui.theme.Blue500
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSuccess(
    navController: NavController,
    ref: String, amount: String, merchantName: String
) {

    val loadingComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.qr_pay_success))
    val successComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.done_success_icon))

    var isLoading by remember { mutableStateOf(true) }

    // Log.d("PaymentSuccess", "Reference Number: $ref, Amount: $amount, Merchant: $merchantName")


    LaunchedEffect(Unit) {
        delay(2500)
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blue200,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(text = "Payment Successful", fontSize = 24.sp, color = Color.White)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = loadingComposition,
                        iterations = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        LottieAnimation(
                            composition = successComposition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Amount:",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "$amount MMK",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(horizontal = 30.dp),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text(
                            text = "Merchant:",
                            fontSize = 20.sp,
                            color = Color.Black,
                        )
                        Text(
                            text = merchantName,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(horizontal = 30.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Reference No:",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Text(
                            text = ref,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = { navController.navigate(NavRoute.HOME) },
                        colors = ButtonColors(
                            containerColor = Blue500.copy(0.8f),
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Continue", color = Color.White, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}