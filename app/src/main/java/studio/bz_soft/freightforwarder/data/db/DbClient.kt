package studio.bz_soft.freightforwarder.data.db

import android.app.Application
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

class DbClient(application: Application) : DbClientInterface {

    private val db by lazy { RoomDB.getDataBase(application) }
    private val locationDao by lazy { db.locationDao() }
    private val tradePointDao by lazy { db.tradePointDao() }
    private val workShiftDao by lazy { db.workShiftDao() }
    private val distanceDao by lazy { db.distanceDao() }

    override suspend fun insertLocation(location: Location) {
        locationDao.insert(location)
    }

    override suspend fun deleteLocation(location: Location) {
        locationDao.delete(location)
    }

    override suspend fun updateLocation(location: Location) {
        locationDao.update(location)
    }

    override suspend fun getAllFromLocations(): List<Location> = locationDao.getAllFromLocation()
    override suspend fun getLastLocation(): Location = locationDao.getLastLocation()

    override suspend fun insertDistance(distance: Distance) {
        distanceDao.insert(distance)
    }

    override suspend fun deleteDistance(distance: Distance) {
        distanceDao.delete(distance)
    }

    override suspend fun updateDistance(distance: Distance) {
        distanceDao.update(distance)
    }

    override suspend fun getAllFromDistance(): List<Distance> = distanceDao.getAllFromDistance()
    override suspend fun getLastDistance(): Distance = distanceDao.getLastDistance()

    override suspend fun insertTradePoint(tradePoint: TradePoint) {
        tradePointDao.insert(tradePoint)
    }

    override suspend fun deleteTradePoint(tradePoint: TradePoint) {
        tradePointDao.delete(tradePoint)
    }

    override suspend fun updateTradePoint(tradePoint: TradePoint) {
        tradePointDao.update(tradePoint)
    }

    override suspend fun getAllFromTradePoint(): List<TradePoint> = tradePointDao.getAllFromTradePoint()
    override suspend fun getTradePointById(recordId: Int): TradePoint = tradePointDao.getTradePointById(recordId)
    override suspend fun getLastRecordId(): Int = tradePointDao.getLastRecordId()

    override suspend fun insertWorkShift(workShift: WorkShift) {
        workShiftDao.insert(workShift)
    }

    override suspend fun deleteWorkShift(workShift: WorkShift) {
        workShiftDao.delete(workShift)
    }

    override suspend fun updateWorkShift(workShift: WorkShift) {
        workShiftDao.update(workShift)
    }

    override suspend fun getAllFromWorkShift(): List<WorkShift> = workShiftDao.getAllFromWorkShift()
    override suspend fun getLastData(): WorkShift = workShiftDao.getLastData()

    override suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int) {
        workShiftDao.updateWorkShift(endDate, endTime, _id)
    }
}