package studio.bz_soft.freightforwarder.data.db

import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

interface DbClientInterface {
    suspend fun insertLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun getAllFromLocations(): List<Location>

    suspend fun insertTradePoint(tradePoint: TradePoint)
    suspend fun deleteTradePoint(tradePoint: TradePoint)
    suspend fun updateTradePoint(tradePoint: TradePoint)
    suspend fun getAllFromTradePoint(): List<TradePoint>
    suspend fun getTradePointById(recordId: Int): TradePoint

    suspend fun insertWorkShift(workShift: WorkShift)
    suspend fun deleteWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(workShift: WorkShift)
    suspend fun getAllFromWorkShift(): List<WorkShift>
    suspend fun getLastData(): WorkShift
    suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int)
}