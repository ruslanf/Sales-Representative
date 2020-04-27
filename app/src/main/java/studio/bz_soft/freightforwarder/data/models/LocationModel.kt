package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class LocationModel(
    @SerializedName("user_id") val userId :Int,
    @SerializedName("work_shift") val workShift :String,
    @SerializedName("latitude") val latitude :Double,
    @SerializedName("longitude") val longitude :Double
)