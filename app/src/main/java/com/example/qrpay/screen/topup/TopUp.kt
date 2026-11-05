package com.example.qrpay.screen.topup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrpay.view.TopUpViewModel


@Composable
fun TopUpPopup(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    val viewModel : TopUpViewModel = hiltViewModel()
    val isLoading = viewModel.isLoading.value
    val showSuccessAnimation = viewModel.showSuccessAnimation.value
    val showFailureAnimation = viewModel.showFailureAnimation.value
    var message by remember { mutableStateOf(viewModel.message.value) }

    // Update the message state when viewModel.message changes
    LaunchedEffect(viewModel.message) {
        message = viewModel.message.value
    }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Top Up", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            TopUpForm(viewModel = viewModel)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                },
                modifier = Modifier.padding(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text("Confirm")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
    )
}

@Composable
fun TopUpForm(viewModel: TopUpViewModel) {
    val phoneNumber = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = phoneNumber.value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            onValueChange = {
                phoneNumber.value = it
                viewModel.phoneNumber.value = it
            },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = amount.value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            onValueChange = {
                amount.value = it
                viewModel.amount.value = it
            },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

