package studio.bz_soft.freightforwarder.ui.root

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class RootController(
    private val repository: RepositoryInterface
) : RootInterface {

    override fun getUserToken(): String? = repository.getUserToken()

    override fun deleteToken() {
        repository.deleteToken()
    }

    override fun deleteUserId() {
        repository.deleteUserId()
    }

    override suspend fun getAllFromTradePoint(): List<TradePoint> = repository.getAllFromTradePoint()

    override suspend fun syncTradePoint(token: String, listStorePoint: List<StorePointModel>): Either<Exception, Unit?> =
        repository.syncTradePoint(token, listStorePoint)
}