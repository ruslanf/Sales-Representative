package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class ImageModel(
    @SerializedName("image") val imageURL: String?
)