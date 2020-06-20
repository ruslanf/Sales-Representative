package studio.bz_soft.freightforwarder.ui.root

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.DistanceModel
import studio.bz_soft.freightforwarder.data.models.LocationModel
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint

interface RootInterface {
    fun getUserToken(): String?
    fun deleteToken()
    fun getUserId(): Int?
    fun deleteUserId()
    fun getWorkShift(): String
    suspend fun getAllFromTradePoint(): List<TradePoint>
    suspend fun getLastDistance(): Distance
    suspend fun getAllFromLocations(): List<Location>
    suspend fun syncTradePoint(token: String, listStorePoint: List<StorePointModel>): Either<Exception, Unit?>
    suspend fun syncTrackDistance(token: String, distanceModel: DistanceModel): Either<Exception, Unit?>
    suspend fun syncTrack(token: String, listLocationModel: List<LocationModel>): Either<Exception, Unit?>
}