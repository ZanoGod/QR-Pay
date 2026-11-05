package com.example.qrpay.qr

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.qrpay.NavRoute
import com.example.qrpay.ui.theme.Blue200
import org.json.JSONException
import org.json.JSONObject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScreen(navController: NavController, onQRCodeScanned: (String) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Scan QR Code",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Blue200)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(1f)
//                            .aspectRatio(1f)
                            .background(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        QRCodeScanner(onQRCodeScanned = { scannedValue ->
                            onQRCodeScanned(scannedValue)
                            Log.d("QRCodeScreen", "Scanned value: $scannedValue")

                            val jsonObject = try {
                                JSONObject(scannedValue)
                            } catch (e: JSONException) {
                                Log.e("QRCodeScreen", "Invalid JSON : $e")
                                null
                            }

                            val refNo = jsonObject?.getString("ref") ?: "N/A"
                            val amount = jsonObject?.getString("amount") ?: "0.0 N/A"

                            navController.navigate(NavRoute.PIN_VERIFY + "/$refNo/$amount")
                        })

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val squareSize = minOf(canvasWidth, canvasHeight) * 0.85f
                            val left = (canvasWidth - squareSize) / 2
                            val top = (canvasHeight - squareSize) / 2
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(left, top),
                                size = Size(squareSize, squareSize),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                        Text(
                            text = "Please position the QR code within the scanner area.",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp).background(color = Color.Gray.copy(0.65f))
                                .drawWithContent {
                                    drawContent()
                                    val stroke = Stroke(
                                        width = 2.dp.toPx(), pathEffect = PathEffect.chainPathEffect(outer = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f), inner = PathEffect.cornerPathEffect(8f))
                                    )
                                    drawRect(
                                        color = Color.Blue,
                                        style = stroke,
                                        size = Size(width = size.width, height = size.height)
                                    )
                                }
                        )
                    }
                }
            }
        }
    )
}