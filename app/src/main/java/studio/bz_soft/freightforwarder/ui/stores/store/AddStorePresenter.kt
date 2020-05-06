package studio.bz_soft.freightforwarder.ui.stores.store

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class AddStorePresenter(
    private val repository: RepositoryInterface
) : AddStoreInterface {

    override fun getUserToken(): String? = repository.getUserToken()
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

    override suspend fun saveSalesPoint(token: String, storePoint: StorePointModel): Either<Exception, Unit?> =
        repository.saveTradePoint(token, storePoint)

    override suspend fun saveSalesPointToDB(tradePoint: TradePoint) =
        repository.insertTradePoint(tradePoint)
}