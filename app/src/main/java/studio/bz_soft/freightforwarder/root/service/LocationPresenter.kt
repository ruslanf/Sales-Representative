package studio.bz_soft.freightforwarder.root.service

import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class LocationPresenter(
    private val repository: RepositoryInterface
) : LocationInterface {
    override suspend fun insertLocation(location: Location) {
        repository.insertLocation(location)
    }
}