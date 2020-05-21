package studio.bz_soft.freightforwarder.ui.shift

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.DistanceModel
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

interface WorkShiftInterface {
    fun getUserToken(): String?
    fun deleteToken()
    fun deleteUserId()

    fun setWorkStarted(isStarted: Boolean)
    fun getWorkStarted(): Boolean?

    fun getWorkShift(): String
    fun currentTime(): String
    fun currentDate(): String

    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>

    suspend fun startWorkShift(workShift: WorkShift)
    suspend fun stopWorkShift(workShift: WorkShift)
    suspend fun update(workShift: WorkShift)
    suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int)
    suspend fun getLastData(): WorkShift

    suspend fun getLastLocation(): Location
    suspend fun startDistance(distance: Distance)
    suspend fun updateDistance(distance: Distance)
    suspend fun getLastDistance(): Distance
    suspend fun sendDistance(token: String, distance: DistanceModel): Either<Exception, Unit?>
}