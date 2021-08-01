package com.modulotech.api.response

import com.google.gson.annotations.SerializedName

data class RecordResponse(
    @field:SerializedName("success") val success: Boolean,
)