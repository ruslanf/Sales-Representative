package studio.bz_soft.freightforwarder.ui.shift

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class WorkShiftPresenter(
    private val repository: RepositoryInterface
) : WorkShiftInterface {

    override fun getUserToken(): String? = repository.getUserToken()

    override fun setWorkStarted(isStarted: Boolean) {
        repository.setWorkStarted(isStarted)
    }

    override fun getWorkStarted(): Boolean? = repository.getWorkStarted()

    override suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel> =
        repository.loadUserProfile(token)
}