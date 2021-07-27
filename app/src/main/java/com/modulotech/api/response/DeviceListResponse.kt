package com.modulotech.api.response


import com.google.gson.annotations.SerializedName

data class DeviceListResponse(
    @field:SerializedName("devices") val devices: List<DeviceResponse>,
)