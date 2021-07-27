package com.modulotech.api.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Nullable

data class DeviceResponse(
    @field:SerializedName("deviceName") val deviceName: String,
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("productType") val productType: String,

    @Nullable
    @field:SerializedName("mode") val mode: String?=null,
    @field:SerializedName("intensity") val intensity: Int = -1,
    @field:SerializedName("position") val position: Int = -1,
    @field:SerializedName("temperature") val temperature: Float = -1f
)
