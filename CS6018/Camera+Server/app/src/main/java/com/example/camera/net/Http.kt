package com.example.camera.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()
}