package studio.bz_soft.freightforwarder.data.repository

import org.koin.core.KoinComponent
import studio.bz_soft.freightforwarder.data.http.ApiClientInterface
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.http.safeRequest
import studio.bz_soft.freightforwarder.data.models.*

class Repository(
    private val storage: LocalStorageInterface,
    private val client: ApiClientInterface
) : RepositoryInterface, KoinComponent {

    override fun setUserToken(userToken: String) {
        storage.setUserToken(userToken)
    }

    override fun getUserToken(): String? = storage.getUserToken()

    override fun deleteToken() {
        storage.deleteToken()
    }

    override fun setUserId(id: Int) {
        storage.setUserId(id)
    }

    override fun deleteUserId() {
        storage.deleteUserId()
    }

    override suspend fun sendRegistrationData(userRequest: UserRequest): Either<Exception, AuthResponseModel> =
        safeRequest { client.signUp(userRequest) }

    override suspend fun sendAuthData(userRequest: UserRequest): Either<Exception, AuthResponseModel> =
        safeRequest { client.signIn(userRequest) }

    override suspend fun restorePassword(email: Email): Either<Exception, Unit?> =
        safeRequest { client.restorePassword(email) }

    override suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?> =
        safeRequest { client.changePassword(token, passwords) }

    override suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel> =
        safeRequest { client.loadUserProfile(token) }

    override suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?> =
        safeRequest { client.updateProfile(token, userProfile) }
}