package studio.bz_soft.freightforwarder.data.db

import android.app.Application
import studio.bz_soft.freightforwarder.data.models.db.Location

class DbClient(application: Application) : DbClientInterface {

    private val db by lazy { RoomDB.getDataBase(application) }
    private val locationDao by lazy { db.locationDao() }

    override suspend fun insertLocation(location: Location) {
        locationDao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: Location) {
        locationDao.deleteLocation(location)
    }

    override suspend fun updateLocation(location: Location) {
        locationDao.updateLocation(location)
    }

    override suspend fun getAllFromLocations(): List<Location> = locationDao.getAllFromLocation()
}