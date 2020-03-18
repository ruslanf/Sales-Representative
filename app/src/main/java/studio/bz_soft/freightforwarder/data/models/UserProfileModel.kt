package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class UserProfileModel(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("email") val email: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("middle_name") val middleName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("manager") val manager: String?
)