package studio.bz_soft.freightforwarder.root.service

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.LocationModel
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location

interface LocationInterface {
    fun getUserToken(): String?
    fun getUserId(): Int?
    fun getWorkStarted(): Boolean?

    fun setStartLatitude(latitude: String)
    fun getStartLatitude(): String?
    fun deleteStartLatitude()
    fun setStartLongitude(longitude: String)
    fun getStartLongitude(): String?
    fun deleteStartLongitude()
    fun setDistance(distance: String)

    suspend fun insertDistance(distance: Distance)
    suspend fun insertLocation(location: Location)
    suspend fun sendLocation(token: String, location: LocationModel): Either<Exception, Unit?>
}