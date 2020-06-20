package studio.bz_soft.freightforwarder.ui.root

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.DistanceModel
import studio.bz_soft.freightforwarder.data.models.LocationModel
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface
import studio.bz_soft.freightforwarder.root.formattedDate
import studio.bz_soft.freightforwarder.root.getCurrentDT
import studio.bz_soft.freightforwarder.root.parseDate

class RootController(
    private val repository: RepositoryInterface
) : RootInterface {

    override fun getUserToken(): String? = repository.getUserToken()

    override fun deleteToken() {
        repository.deleteToken()
    }

    override fun getUserId(): Int? = repository.getUserId()

    override fun deleteUserId() {
        repository.deleteUserId()
    }

    override fun getWorkShift(): String = formattedDate(parseDate(getCurrentDT()))

    override suspend fun getAllFromTradePoint(): List<TradePoint> = repository.getAllFromTradePoint()

    override suspend fun getLastDistance(): Distance = repository.getLastDistance()

    override suspend fun getAllFromLocations(): List<Location> = repository.getAllFromLocations()

    override suspend fun syncTradePoint(token: String, listStorePoint: List<StorePointModel>): Either<Exception, Unit?> =
        repository.syncTradePoint(token, listStorePoint)

    override suspend fun syncTrackDistance(token: String, distanceModel: DistanceModel): Either<Exception, Unit?> =
        repository.syncTrackDistance(token, distanceModel)

    override suspend fun syncTrack(token: String, listLocationModel: List<LocationModel>): Either<Exception, Unit?> =
        repository.syncTrack(token, listLocationModel)
}