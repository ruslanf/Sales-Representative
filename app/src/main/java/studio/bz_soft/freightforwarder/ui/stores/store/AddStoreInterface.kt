package studio.bz_soft.freightforwarder.ui.stores.store

import okhttp3.MultipartBody
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.ImageModel

interface AddStoreInterface {
    fun getUserToken(): String?
    suspend fun uploadImage(token: String, image: MultipartBody.Part): Either<Exception, List<ImageModel>>
}