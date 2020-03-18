package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.*

interface RepositoryInterface {
    fun setUserToken(userToken: String)
    fun getUserToken(): String?
    fun deleteToken()

    fun setUserId(id: Int)
    fun deleteUserId()

    suspend fun sendRegistrationData(userRequest: UserRequest): Either<Exception, AuthResponseModel>
    suspend fun sendAuthData(userRequest: UserRequest): Either<Exception, AuthResponseModel>

    suspend fun restorePassword(email: Email): Either<Exception, Unit?>
    suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?>

    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>
    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?>
}