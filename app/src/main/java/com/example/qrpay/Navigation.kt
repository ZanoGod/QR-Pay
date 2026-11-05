package com.example.qrpay

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.qrpay.qr.QRCodeScreen
import com.example.qrpay.screen.HomeScreen
import com.example.qrpay.screen.LoginScreen
import com.example.qrpay.screen.PaymentSuccess
import com.example.qrpay.screen.PinVerifyScreen
import com.example.qrpay.screen.ProfileScreen
import com.example.qrpay.screen.RegisterScreen
import com.example.qrpay.screen.TransactionHistoryScreen
import com.example.qrpay.screen.TransferScreen
import com.example.qrpay.ui.theme.Blue500
import com.example.qrpay.view.LoginViewModel
import kotlinx.coroutines.flow.firstOrNull


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()


    // Determine start destination before showing NavHost
    val startDestination by produceState(initialValue = null as String?) {
        val token = loginViewModel.dataLogin.getAuthToken().firstOrNull()
        value = if (token != null && loginViewModel.isValidToken(token)) {
            NavRoute.HOME
        } else {
            NavRoute.LOGIN
        }
    }

    // Show loading until the start destination is determined
    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(color = colorResource(R.color.ic_launcher_background)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painterResource(R.drawable.qr_code_splash_xxhdpi), contentDescription = null, tint = Color.Unspecified)
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Blue500)
                Text("Loading credentials...")
            }
        }
       return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!,
    ) {
        composable(NavRoute.LOGIN) {
            LoginScreen(navController)
        }
        composable(NavRoute.HOME) { HomeScreen(navController) }
        composable(NavRoute.QR_CODE_SCANNER) {
            QRCodeScreen(navController) { scannedValue ->
                Log.e("QRCodeScannerResult", "Scanned QR Code: $scannedValue")
            }
        }
        composable(NavRoute.REGISTER) {
            RegisterScreen(navController)
        }
        composable(
            route = NavRoute.PIN_VERIFY + "/{ref}/{amount}",
            arguments = listOf(
                navArgument("ref") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType })
        ) { backStackEntry ->
            val ref = backStackEntry.arguments?.getString("ref") ?: "N/A"
            val amount = backStackEntry.arguments?.getString("amount") ?: "0.0 or N/A"
            PinVerifyScreen(navController, ref, amount)
        }
        composable(
            NavRoute.SUCCESS + "/{ref}/{amount}/{merchantName}",
            arguments = listOf(
                navArgument("ref") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("merchantName") { type = NavType.StringType })
        ) { backStackEntry ->
            val ref = backStackEntry.arguments?.getString("ref") ?: "N/A"
            val amount = backStackEntry.arguments?.getString("amount") ?: "0.0 or N/A"
            val merchantName = backStackEntry.arguments?.getString("merchantName") ?: "N/A"
            PaymentSuccess(navController, ref, amount, merchantName)
        }

        composable(NavRoute.HISTORY) { TransactionHistoryScreen(navController) }

        composable(NavRoute.TRANSFER) { TransferScreen(navController) }

        composable(NavRoute.PROFILE) { ProfileScreen(navController) }
    }
}


object NavRoute {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val QR_CODE_SCANNER = "qr_scanner"
    const val PIN_VERIFY = "pin_verify"
    const val SUCCESS = "pay_success"
    const val HISTORY = "history"
    const val TRANSFER = "transfer"
    const val PROFILE = "profile"
}
