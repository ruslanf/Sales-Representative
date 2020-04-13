package studio.bz_soft.freightforwarder.data.db

import android.app.Application
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.Outlet

class DbClient(application: Application) : DbClientInterface {

    private val db by lazy { RoomDB.getDataBase(application) }
    private val locationDao by lazy { db.locationDao() }
    private val outletDao by lazy { db.outletDao() }

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

    override suspend fun insertOutlet(outlet: Outlet) {
        outletDao.insert(outlet)
    }

    override suspend fun deleteOutlet(outlet: Outlet) {
        outletDao.delete(outlet)
    }

    override suspend fun updateOutlet(outlet: Outlet) {
        outletDao.update(outlet)
    }

    override suspend fun getAllFromOutlet(outlet: Outlet) = outletDao.getAllFromOutlet()
}