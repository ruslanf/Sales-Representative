package studio.bz_soft.freightforwarder.ui.stores

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class StoresPresenter(
    private val repository: RepositoryInterface
) : StoresInterface {
    override fun getUserToken(): String? = repository.getUserToken()
    override fun getWorkStarted(): Boolean? = repository.getWorkStarted()
    override suspend fun getLastLocation(): Location = repository.getLastLocation()

    override suspend fun getTradePointList(token: String): Either<Exception, List<StorePointModel>> =
        repository.getTradePointList(token)
}