package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class ManagersModel(
    @SerializedName("manager") val manager: String?
)