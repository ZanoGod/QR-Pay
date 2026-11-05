package com.example.qrpay.db

import android.util.Log
import com.example.qrpay.apiservice.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://103.253.146.97:3002/api/"

    //  private const val BASE_URL = "http://192.168.1.16:3002/api/"

    @Provides
    @Singleton
    fun provideApiService(dataLoginManager: DataLoginManager): ApiService {

        val token = runBlocking { dataLoginManager.getAuthToken() }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val response = chain.proceed(
                originalRequest.newBuilder().apply {
                    header("Authorization:", "Bearer $token")
                }.build()
            )
            if (response.code == 404) {
                Log.e("NetworkModule", "Unauthorized request: ${response.message}")
            }
            return@Interceptor response
        }

        val contentTypeInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestWithContentType = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(requestWithContentType)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(contentTypeInterceptor)
            //    .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}
