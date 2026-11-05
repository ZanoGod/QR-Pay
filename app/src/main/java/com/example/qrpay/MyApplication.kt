package com.example.qrpay

import android.app.Application
import com.example.qrpay.db.DataLoginManager
import com.example.qrpay.repo.ApiRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var dataLoginManager: DataLoginManager

    lateinit var apiRepository: ApiRepository

    override fun onCreate() {
        super.onCreate()

    }
}
