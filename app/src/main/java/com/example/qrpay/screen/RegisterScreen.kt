package com.example.qrpay.screen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrpay.NavRoute
import com.example.qrpay.R
import com.example.qrpay.ui.theme.Blue200
import com.example.qrpay.ui.theme.Blue500
import com.example.qrpay.ui.theme.Blue700
import com.example.qrpay.ui.theme.Green
import com.example.qrpay.view.RegisterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {

//    var name by remember { mutableStateOf("JackJack") }
//    var phoneNumber by remember { mutableStateOf("0922334455") }
//    var pin by remember { mutableStateOf("1234") }
//    var accountNumber by remember { mutableStateOf("222233334444") }
//    var bankName by remember { mutableStateOf("TH Bank") }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }

    val registerResult by viewModel.registerResult
    val isLoading by viewModel.isLoading
    val showSuccessAnimation by viewModel.showSuccessAnimation
    var showTopSnackBar by remember { mutableStateOf(false) }
    var topSnackBarMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    //for back handler
    var backPressedOnce by remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    LaunchedEffect(showTopSnackBar) {
        if (showTopSnackBar) {
            delay(2000)
            showTopSnackBar = false
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Register", style = MaterialTheme.typography.headlineSmall) },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                        }
                    }
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    maxLines = 1,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    maxLines = 1,
                    onValueChange = {
                        if (it.length <= 11) {
                            phoneNumber = it
                        }
                    },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    maxLines = 1,
                    onValueChange = {
                        if (it.length <= 4) {
                            pin = it
                        }
                    },
                    label = { Text("PIN") },
                    visualTransformation = PasswordVisualTransformation(mask = '*'),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = accountNumber,
                    maxLines = 1,
                    onValueChange = {
                        if (it.length <= 12) {
                            accountNumber = it
                        }
                    },
                    label = { Text("Account Number") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bankName,
                    maxLines = 1,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    colors = ButtonColors(
                        containerColor = Blue500.copy(0.8f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    ),
                    onClick = {
                        if (name.isBlank() || phoneNumber.isBlank() || pin.isBlank() || accountNumber.isBlank() || bankName.isBlank()) {
                            topSnackBarMessage = "Please fill all fields."
                            showTopSnackBar = true
                            return@Button
                        }
                        coroutineScope.launch {
                            viewModel.name.value = name
                            viewModel.phoneNumber.value = phoneNumber
                            viewModel.pin.value = pin
                            viewModel.accountNumber.value = accountNumber
                            viewModel.bankName.value = bankName
                            viewModel.registerUser()
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Blue700)
                    } else {
                        Text("Register")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LoginText { navController.navigate(NavRoute.LOGIN) }
            }

            if (showSuccessAnimation) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.reg_success_animation))
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
        LaunchedEffect(registerResult) {
            registerResult?.fold(
                onSuccess = {
                    topSnackBarMessage = "Registration successful!"
                    showTopSnackBar = true
                    delay(2000)
                    showTopSnackBar = false
                },
                onFailure = {
                    topSnackBarMessage = "Registration failed: ${it.message}"
                    showTopSnackBar = true
                }
            )
        }
    }
}

@Composable
fun LoginText(onLoginClick: () -> Unit) {
    Row {
        Text("Already have an account?", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Login",
            fontSize = 16.sp,
            modifier = Modifier.clickable { onLoginClick() },
            fontWeight = FontWeight.Bold,
            color = Green,
            textDecoration = TextDecoration.Underline
        )
    }
}
