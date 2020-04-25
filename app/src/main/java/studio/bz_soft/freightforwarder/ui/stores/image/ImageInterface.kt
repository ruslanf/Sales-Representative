package studio.bz_soft.freightforwarder.ui.stores.image

import okhttp3.MultipartBody
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.ImageModel

interface ImageInterface {
    fun getUserToken(): String?
    fun setImagesSaved(isImagesSaved: Boolean)
    fun setImageOutside(image: String)
    fun setImageInside(image: String)
    fun setImageAssortment(image: String)
    fun setImageCorner(image: String)
    suspend fun uploadImage(token: String, image: MultipartBody.Part): Either<Exception, List<ImageModel>>
}