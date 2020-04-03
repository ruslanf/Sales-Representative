package studio.bz_soft.freightforwarder.ui.stores.store

import okhttp3.MultipartBody
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.ImageModel
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class AddStorePresenter(
    private val repository: RepositoryInterface
) : AddStoreInterface {

    override fun getUserToken(): String? = repository.getUserToken()

    override suspend fun uploadImage(token: String, image: MultipartBody.Part): Either<Exception, List<ImageModel>> =
        repository.uploadImage(token, image)
}