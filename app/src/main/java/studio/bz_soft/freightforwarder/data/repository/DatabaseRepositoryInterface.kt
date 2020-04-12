package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.models.db.Location

interface DatabaseRepositoryInterface {
    suspend fun insertLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun getAllFromLocations(): List<Location>
}