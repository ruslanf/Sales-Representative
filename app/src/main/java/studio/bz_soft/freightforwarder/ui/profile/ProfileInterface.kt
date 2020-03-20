package studio.bz_soft.freightforwarder.ui.profile

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.Passwords
import studio.bz_soft.freightforwarder.data.models.UserProfileModel

interface ProfileInterface {
    fun getUserToken(): String?
    fun getUserId(): Int?

    suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?>

    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>
    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?>
}