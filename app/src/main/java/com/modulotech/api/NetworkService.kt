package com.modulotech.api

import com.modulotech.api.response.RecordResponse
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface NetworkService {
    @Multipart
    @POST("conversations")
    suspend fun uploadRecord(
        @Header("x-access-token") token: String,
        @Part("customerPhoneNo") customerPhoneNo: String,
        @Part("type") type: String,
        @Part("telesalePhoneNo") salePhoneNo: String,
        @Part("calledAt") calledAt: String,
        @Part file: MultipartBody.Part,
    ): RecordResponse
}