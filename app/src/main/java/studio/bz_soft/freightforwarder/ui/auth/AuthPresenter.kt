package studio.bz_soft.freightforwarder.ui.auth

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.AuthResponseModel
import studio.bz_soft.freightforwarder.data.models.Email
import studio.bz_soft.freightforwarder.data.models.UserRequest
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class AuthPresenter(
    private val repository: RepositoryInterface
) : AuthInterface {

    override fun getUserToken(): String? =
        repository.getUserToken()

    override fun setUserToken(token: String) {
        repository.setUserToken(token)
    }

    override fun setUserId(id: Int) {
        repository.setUserId(id)
    }

    override suspend fun signUpUser(eMail: String, pass: String): Either<Exception, AuthResponseModel> =
        repository.sendRegistrationData(UserRequest(eMail, pass))

    override suspend fun signInUser(eMail: String, pass: String): Either<Exception, AuthResponseModel> =
        repository.sendAuthData(UserRequest(eMail, pass))

    override suspend fun restorePassword(eMail: String): Either<Exception, Unit?> =
        repository.restorePassword(Email(eMail))
}