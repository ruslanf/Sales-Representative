package studio.bz_soft.freightforwarder.ui.stores.edit

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class EditStorePresenter(
    private val repository: RepositoryInterface
) : EditStoreInterface {
    override fun getUserId(): Int? = repository.getUserId()

    override fun getImageOutside(): String? = repository.getImageOutside()
    override fun deleteImageOutside() {
        repository.deleteImageOutside()
    }

    override fun getImageInside(): String? = repository.getImageInside()
    override fun deleteImageInside() {
        repository.deleteImageInside()
    }

    override fun getImageAssortment(): String? = repository.getImageAssortment()
    override fun deleteImageAssortment() {
        repository.deleteImageAssortment()
    }

    override fun getImageCorner(): String? = repository.getImageCorner()
    override fun deleteImageCorner() {
        repository.deleteImageCorner()
    }

    override suspend fun getTradePoint(token: String, id: Int): Either<Exception, StorePointModel> =
        repository.getTradePoint(token, id)

    override suspend fun updateTradePoint(token: String, id: Int, storePoint: StorePointModel): Either<Exception, Unit?> =
        repository.updateTradePoint(token, id, storePoint)
}