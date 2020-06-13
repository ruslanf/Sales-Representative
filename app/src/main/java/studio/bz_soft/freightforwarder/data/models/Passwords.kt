package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class Passwords(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String
)