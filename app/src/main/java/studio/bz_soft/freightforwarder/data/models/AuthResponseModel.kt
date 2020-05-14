package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class AuthResponseModel(
    @SerializedName("id") val id: Int?,
    @SerializedName("token") val token: String?
)