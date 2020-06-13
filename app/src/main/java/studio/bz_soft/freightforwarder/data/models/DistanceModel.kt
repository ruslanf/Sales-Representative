package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class DistanceModel(
    @SerializedName("user_id") val userId :Int?,
    @SerializedName("work_shift") val workShift :String?,
    @SerializedName("distance") val distance: Float?
)