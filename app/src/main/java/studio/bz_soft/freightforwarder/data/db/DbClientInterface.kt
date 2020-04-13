package studio.bz_soft.freightforwarder.data.db

import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.Outlet

interface DbClientInterface {
    suspend fun insertLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun getAllFromLocations(): List<Location>

    suspend fun insertOutlet(outlet: Outlet)
    suspend fun deleteOutlet(outlet: Outlet)
    suspend fun updateOutlet(outlet: Outlet)
    suspend fun getAllFromOutlet(): List<Outlet>
}