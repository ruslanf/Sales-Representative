package studio.bz_soft.freightforwarder.ui.shift

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.UserProfileModel

interface WorkShiftInterface {
    fun getUserToken(): String?
    fun setWorkStarted(isStarted: Boolean)
    fun getWorkStarted(): Boolean?

    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>
}