package studio.bz_soft.freightforwarder.root.service

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.LocationModel
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class LocationController(
    private val repository: RepositoryInterface
) : LocationInterface {
    override fun getUserToken(): String? = repository.getUserToken()
    override fun getUserId(): Int? = repository.getUserId()
    override fun getWorkStarted(): Boolean? = repository.getWorkStarted()

    override suspend fun insertLocation(location: Location) {
        repository.insertLocation(location)
    }

    override suspend fun sendLocation(token: String, location: LocationModel): Either<Exception, Unit?> =
        repository.sendLocation(token, location)
}