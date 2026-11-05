package com.example.qrpay.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrpay.ui.theme.Blue500


@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("⌫", "0", "✔"),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp), // Add padding to avoid overlap with system bar
        verticalArrangement = Arrangement.SpaceBetween, // Ensure even spacing between rows
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), // Add horizontal padding
                horizontalArrangement = Arrangement.SpaceEvenly // Space buttons evenly in the row
            ) {
                row.forEach { button ->
                    PinPad(
                        text = button,
                        modifier = Modifier
                            .weight(1f) // Distribute the weight evenly among buttons
                            .padding(5.dp)
                            .aspectRatio(1f), // Ensure buttons are square
                        onClick = {
                            when (button) {
                                "⌫" -> onClearClick()
                                "✔" -> onConfirmClick()
                                else -> onNumberClick(button)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PinPad(
    text: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val buttonStyle = remember(text) {
        ButtonStyle(
            backgroundColor = when (text) {
                "✔" -> Blue500.copy(0.8f)
                "⌫" -> Color.Gray.copy(0.2f)
                else -> Color.White
            },
            textColor = when (text) {
                "✔" -> Color.White
                else -> Color.Black
            },
            fontSize = when (text) {
                "✔" -> 30.sp
                "⌫" -> 26.sp
                else -> 25.sp
            }
        )
    }

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(25),
        colors = ButtonDefaults.buttonColors(containerColor = buttonStyle.backgroundColor),
        modifier = modifier,
    ) {
        Text(
            text = text,
            fontSize = buttonStyle.fontSize,
            color = buttonStyle.textColor,
        )
    }
}

data class ButtonStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val fontSize: TextUnit
)