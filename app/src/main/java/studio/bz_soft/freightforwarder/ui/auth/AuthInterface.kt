package studio.bz_soft.freightforwarder.ui.auth

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.AuthResponseModel

interface AuthInterface {
    fun setUserToken(token: String)
    fun setUserId(id: Int)
    suspend fun signUpUser(eMail: String, pass: String): Either<Exception, AuthResponseModel>
    suspend fun signInUser(eMail: String, pass: String): Either<Exception, AuthResponseModel>
}