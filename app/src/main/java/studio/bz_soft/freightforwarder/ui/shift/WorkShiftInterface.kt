package studio.bz_soft.freightforwarder.ui.shift

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

interface WorkShiftInterface {
    fun getUserToken(): String?
    fun setWorkStarted(isStarted: Boolean)
    fun getWorkStarted(): Boolean?

    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>

    suspend fun startWorkShift(workShift: WorkShift)
    suspend fun stopWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int)
    suspend fun getLastData(): WorkShift
}