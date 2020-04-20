package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.Outlet
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

interface DatabaseRepositoryInterface {
    suspend fun insertLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun getAllFromLocations(): List<Location>

    suspend fun insertOutlet(outlet: Outlet)
    suspend fun deleteOutlet(outlet: Outlet)
    suspend fun updateOutlet(outlet: Outlet)
    suspend fun getAllFromOutlet(): List<Outlet>

    suspend fun insertWorkShift(workShift: WorkShift)
    suspend fun deleteWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int)
    suspend fun getAllFromWorkShift(): List<WorkShift>
    suspend fun getLastData(): WorkShift
}