package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
