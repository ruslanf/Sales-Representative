package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

interface DatabaseRepositoryInterface {
    suspend fun insertLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun getAllFromLocations(): List<Location>
    suspend fun getLastLocation(): Location

    suspend fun insertDistance(distance: Distance)
    suspend fun deleteDistance(distance: Distance)
    suspend fun updateDistance(distance: Distance)
    suspend fun getAllFromDistance(): List<Distance>
    suspend fun getLastDistance(): Distance

    suspend fun insertTradePoint(tradePoint: TradePoint)
    suspend fun deleteTradePoint(tradePoint: TradePoint)
    suspend fun updateTradePoint(tradePoint: TradePoint)
    suspend fun getAllFromTradePoint(): List<TradePoint>
    suspend fun getTradePointById(id: Int): TradePoint
    suspend fun getLastRecordId(): Int

    suspend fun insertWorkShift(workShift: WorkShift)
    suspend fun deleteWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int)
    suspend fun getAllFromWorkShift(): List<WorkShift>
    suspend fun getLastData(): WorkShift
}