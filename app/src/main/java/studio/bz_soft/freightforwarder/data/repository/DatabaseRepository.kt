package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.db.DbClientInterface
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.Outlet
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

    override suspend fun insertOutlet(outlet: Outlet) {
        dbClient.insertOutlet(outlet)
    }

    override suspend fun deleteOutlet(outlet: Outlet) {
        dbClient.deleteOutlet(outlet)
    }

    override suspend fun updateOutlet(outlet: Outlet) {
        dbClient.updateOutlet(outlet)
    }

    override suspend fun getAllFromOutlet(): List<Outlet> = dbClient.getAllFromOutlet()

    override suspend fun insertWorkShift(workShift: WorkShift) {
        dbClient.insertWorkShift(workShift)
    }

    override suspend fun deleteWorkShift(workShift: WorkShift) {
        dbClient.deleteWorkShift(workShift)
    }

    override suspend fun updateWorkShift(workShift: WorkShift) {
        dbClient.updateWorkShift(workShift)
    }

    override suspend fun getAllFromWorkShift(): List<WorkShift> = dbClient.getAllFromWorkShift()
}