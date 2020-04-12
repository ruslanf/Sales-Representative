package studio.bz_soft.freightforwarder.root.service

import studio.bz_soft.freightforwarder.data.models.db.Location

interface LocationInterface {
    suspend fun insertLocation(location: Location)
}