package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.db.DbClientInterface
import studio.bz_soft.freightforwarder.data.models.db.Location

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
}