package studio.bz_soft.freightforwarder.data.db

import studio.bz_soft.freightforwarder.data.models.db.Location

interface DbClientInterface {
    suspend fun insertLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun getAllFromLocations(): List<Location>
}