package studio.bz_soft.freightforwarder.ui.stores

import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class StoresPresenter(
    private val repository: RepositoryInterface
) : StoresInterface {
    override fun getWorkStarted(): Boolean? = repository.getWorkStarted()
    override suspend fun getLastLocation(): Location = repository.getLastLocation()
}