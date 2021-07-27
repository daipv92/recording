package com.modulotech.api

import com.modulotech.api.response.DeviceListResponse
import retrofit2.http.GET

interface NetworkService {
    @GET("modulotest/data.json")
    suspend fun fetchDevices(): DeviceListResponse
}