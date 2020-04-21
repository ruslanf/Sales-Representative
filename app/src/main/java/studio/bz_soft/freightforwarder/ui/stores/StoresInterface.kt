package studio.bz_soft.freightforwarder.ui.stores

import studio.bz_soft.freightforwarder.data.models.db.Location

interface StoresInterface {
    fun getWorkStarted(): Boolean?
    suspend fun getLastLocation(): Location
}