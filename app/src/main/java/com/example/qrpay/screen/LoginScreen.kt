package com.example.qrpay.screen

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.qrpay.NavRoute
import com.example.qrpay.ui.theme.Blue200
import com.example.qrpay.ui.theme.Blue500
import com.example.qrpay.ui.theme.Green
import com.example.qrpay.view.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    //  var phoneNumber by remember { mutableStateOf("0922334455") }
    // var pin by remember { mutableStateOf("1234") }
    var phoneNumber by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val loginUiState by viewModel.loginResult
    val isLoading by viewModel.isLoading

    val snackBarHostState = remember { SnackbarHostState() }
    //for back handler
    var backPressedOnce by remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var topSnackBarMessage by remember { mutableStateOf("") }
    var showTopSnackBar by remember { mutableStateOf(false) }


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
            delay(2400L)
            showTopSnackBar = false
        }
    }

    LaunchedEffect(loginUiState) {
        loginUiState?.let { result ->
            if (result.isSuccess) {
                navController.navigate(NavRoute.HOME) {
                    popUpTo(NavRoute.LOGIN) { inclusive = true }
                }
            } else {
                result.onFailure {
                    Log.e("LoginScreen", "Login failed: ${it.message}")
                    topSnackBarMessage = "Login failed: ${it.message}"
                    showTopSnackBar = true
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                AnimatedVisibility(
                    modifier = Modifier.padding(top = 50.dp),
                    visible = showTopSnackBar,
                    enter = slideInVertically(
                        animationSpec = tween(700),
                        initialOffsetY = { -it }
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(700),
                        targetOffsetY = { -it }
                    )
                ) {
                    Snackbar(
                        modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp),
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
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login", style = MaterialTheme.typography.headlineSmall) },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                    value = phoneNumber,
                    onValueChange = {
                        if(it.length <= 11){
                         phoneNumber = it
                        }
                    },
                    label = { Text("Phone Number") },
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
                    }, label = { Text("PIN") },
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
                        if (phoneNumber.isBlank() || pin.isBlank()) {
                            topSnackBarMessage = "Empty Fields!! \n \n Please fill all fields."
                            showTopSnackBar = true
                            return@Button
                        }
                        keyboardController?.hide()
                        viewModel.updatePhoneNumber(phoneNumber)
                        viewModel.updatePin(pin)
                        viewModel.login()
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Blue500.copy(0.8f))
                    } else {
                        Text("Login")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                RegisterText { navController.navigate(NavRoute.REGISTER) }

            }
        }
    }
}

@Composable
fun RegisterText(onLoginClick: () -> Unit) {
    Row {
        Text("Don't have an account? ", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Register",
            fontSize = 16.sp,
            modifier = Modifier.clickable { onLoginClick() },
            fontWeight = FontWeight.Bold,
            color = Green,
            textDecoration = TextDecoration.Underline
        )
    }
}

