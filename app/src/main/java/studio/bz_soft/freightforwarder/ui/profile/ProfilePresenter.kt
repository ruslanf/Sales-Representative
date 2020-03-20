package studio.bz_soft.freightforwarder.ui.profile

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.Passwords
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class ProfilePresenter(
    private val repository: RepositoryInterface
) : ProfileInterface {

    override fun getUserToken(): String? = repository.getUserToken()

    override fun getUserId(): Int? = repository.getUserId()

    override suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?> =
        repository.changePassword(token, passwords)

    override suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel> =
        repository.loadUserProfile(token)

    override suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?> =
        repository.updateProfile(token, userProfile)
}