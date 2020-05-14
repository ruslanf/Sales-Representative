package studio.bz_soft.freightforwarder.data.db

import kotlinx.coroutines.Job
import studio.bz_soft.freightforwarder.data.models.db.Location

interface LocationViewModelInterface {
    fun insert(location: Location): Job
    fun delete(location: Location): Job
    fun update(location: Location): Job
    fun getAllFromLocation(): Job
}