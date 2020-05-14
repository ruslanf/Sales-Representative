package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.db.DbClientInterface
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

class DatabaseRepository(private val dbClient: DbClientInterface) : DatabaseRepositoryInterface {

    override suspend fun insertLocation(location: Location) {
        dbClient.insertLocation(location)
    }

    override suspend fun deleteLocation(location: Location) {
        dbClient.deleteLocation(location)
    }

    override suspend fun updateLocation(location: Location) {
        dbClient.updateLocation(location)
    }

    override suspend fun getAllFromLocations(): List<Location> = dbClient.getAllFromLocations()
    override suspend fun getLastLocation(): Location = dbClient.getLastLocation()
    override suspend fun getLastRecordId(): Int = dbClient.getLastRecordId()

    override suspend fun insertTradePoint(tradePoint: TradePoint) {
        dbClient.insertTradePoint(tradePoint)
    }

    override suspend fun deleteTradePoint(tradePoint: TradePoint) {
        dbClient.deleteTradePoint(tradePoint)
    }

    override suspend fun updateTradePoint(tradePoint: TradePoint) {
        dbClient.updateTradePoint(tradePoint)
    }

    override suspend fun getAllFromTradePoint(): List<TradePoint> = dbClient.getAllFromTradePoint()

    override suspend fun getTradePointById(id: Int): TradePoint = dbClient.getTradePointById(id)

    override suspend fun insertWorkShift(workShift: WorkShift) {
        dbClient.insertWorkShift(workShift)
    }

    override suspend fun deleteWorkShift(workShift: WorkShift) {
        dbClient.deleteWorkShift(workShift)
    }

    override suspend fun updateWorkShift(workShift: WorkShift) {
        dbClient.updateWorkShift(workShift)
    }

    override suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int) {
        dbClient.updateWorkShift(endDate, endTime, _id)
    }

    override suspend fun getAllFromWorkShift(): List<WorkShift> = dbClient.getAllFromWorkShift()

    override suspend fun getLastData(): WorkShift = dbClient.getLastData()
}