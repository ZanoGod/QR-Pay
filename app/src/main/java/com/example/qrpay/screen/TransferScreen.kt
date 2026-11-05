package com.example.qrpay.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrpay.R
import com.example.qrpay.ui.theme.Blue200
import com.example.qrpay.ui.theme.Blue500
import com.example.qrpay.view.TransferViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    navController: NavController,
    viewModel: TransferViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var recPhoneNumber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val showSuccessAnimation by viewModel.showSuccessAnimation
    val isLoading by viewModel.isLoading
    val transferResult by viewModel.transferResult
    var topSnackBarMessage by remember { mutableStateOf("") }
    var showTopSnackBar by remember { mutableStateOf(false) }


    fun clearText() {
        recPhoneNumber = ""
        amount = ""
        pin = ""
    }

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
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blue200,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(text = "Transfer", fontSize = 24.sp, color = Color.White)
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = recPhoneNumber,
                    onValueChange = { if(it.length <= 11){
                        recPhoneNumber = it
                    } },
                    label = { Text("Receiver Phone Number") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 4) {
                            pin = it
                        }
                    },
                    label = { Text("PIN") },
                    visualTransformation = PasswordVisualTransformation(mask = '*'),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    colors = ButtonColors(
                        containerColor = Blue500.copy(0.8f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    ),
                    onClick = {
                        if (pin.isBlank() || recPhoneNumber.isBlank() || amount.isBlank()) {
                            topSnackBarMessage = "Empty Field!!"
                            showTopSnackBar = true
                            return@Button
                        }
                        keyboardController?.hide()
                        viewModel.phoneNumber.value = recPhoneNumber
                        viewModel.amount.value = amount
                        viewModel.pin.value = pin
                        viewModel.transferAmount()
                        clearText()
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Blue500.copy(0.8f))
                    } else {
                        Text("Confirm")
                    }
                }
                LaunchedEffect(showTopSnackBar) {
                    if (showTopSnackBar) {
                        delay(2000L)
                        showTopSnackBar = false
                    }
                }
            }
            if (showSuccessAnimation) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.trans_success))
                LottieAnimation(
                    composition = composition,
                    iterations = 1,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
                LaunchedEffect(Unit) {
                    delay(2000)
                    viewModel.showSuccessAnimation.value = false
                }
            }
            AnimatedVisibility(
                modifier = Modifier.padding(innerPadding),
                visible = showTopSnackBar,
                enter = slideIn(
                    animationSpec = tween(700),
                    initialOffset = { fullSize -> IntOffset(0, -fullSize.height) }),
                exit = slideOut { IntOffset(0, -it.height) }
            ) {
                Snackbar(
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp),
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
        LaunchedEffect(transferResult) {
            transferResult?.fold(
                onSuccess = {
                    topSnackBarMessage = "Transfer successful!"
                    showTopSnackBar = true
                    delay(2000)
                    showTopSnackBar = false
                },
                onFailure = {
                    topSnackBarMessage = "Transfer failed: ${it.message}"
                    showTopSnackBar = true
                }
            )
        }
    }
}


