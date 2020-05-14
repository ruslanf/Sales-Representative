package studio.bz_soft.freightforwarder.ui.shift

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.data.models.db.WorkShift
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class WorkShiftPresenter(
    private val repository: RepositoryInterface
) : WorkShiftInterface {

    override fun getUserToken(): String? = repository.getUserToken()

    override fun setWorkStarted(isStarted: Boolean) {
        repository.setWorkStarted(isStarted)
    }

    override fun getWorkStarted(): Boolean? = repository.getWorkStarted()

    override fun deleteToken() {
        repository.deleteToken()
    }

    override fun deleteUserId() {
        repository.deleteUserId()
    }

    override suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel> =
        repository.loadUserProfile(token)

    override suspend fun startWorkShift(workShift: WorkShift) {
        repository.insertWorkShift(workShift)
    }

    override suspend fun stopWorkShift(workShift: WorkShift) {
        repository.updateWorkShift(workShift)
    }

    override suspend fun update(workShift: WorkShift) {
        repository.updateWorkShift(workShift)
    }

    override suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int) {
        repository.updateWorkShift(endDate, endTime, _id)
    }

    override suspend fun getLastData(): WorkShift = repository.getLastData()
}