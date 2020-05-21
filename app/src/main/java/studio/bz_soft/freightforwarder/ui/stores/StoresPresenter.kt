package studio.bz_soft.freightforwarder.ui.stores

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface
import studio.bz_soft.freightforwarder.root.formattedDate
import studio.bz_soft.freightforwarder.root.getCurrentDT
import studio.bz_soft.freightforwarder.root.parseDate

class StoresPresenter(
    private val repository: RepositoryInterface
) : StoresInterface {
    override fun getUserToken(): String? = repository.getUserToken()
    override fun getWorkStarted(): Boolean? = repository.getWorkStarted()
    override fun getWorkShift(): String = formattedDate(parseDate(getCurrentDT()))
    override fun currentDate(): String = formattedDate(parseDate(getCurrentDT()))

    override suspend fun getLastLocation(): Location = repository.getLastLocation()

    override suspend fun getTradePointList(token: String): Either<Exception, List<StorePointModel>> =
        repository.getTradePointList(token)
}