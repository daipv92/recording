package com.modulotech.api

import com.modulotech.utilities.Logger
import com.modulotech.utilities.TOKEN
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class NetworkAPI {
    suspend fun uploadRecordFile(customerPhone: String, type: String, salePhone: String, createAt: String , fileUri: String) : Boolean {
        val networkService = provideNetworkService()

        val file = File(fileUri)
        val requestFile: RequestBody = file
            .asRequestBody("audio/mpeg".toMediaTypeOrNull())

        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return try {
            val response = networkService.uploadRecord(TOKEN, customerPhone, type, salePhone, createAt, filePart)
            Logger.i("uploadRecordFile, success = ${response.success}")
            response.success
        } catch (e: Exception) {
            Logger.e("uploadRecordFile, $e")
            false
        }
    }

    private fun provideNetworkService(): NetworkService {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkService::class.java)
    }

    companion object {
        private const val BASE_URL = "https://damin-api.reops.cloud"
    }
}