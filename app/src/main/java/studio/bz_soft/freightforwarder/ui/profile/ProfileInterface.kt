package studio.bz_soft.freightforwarder.ui.profile

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.ManagersModel
import studio.bz_soft.freightforwarder.data.models.Passwords
import studio.bz_soft.freightforwarder.data.models.UserProfileModel

interface ProfileInterface {
    fun getUserToken(): String?
    fun getUserId(): Int?

    fun deleteToken()
    fun deleteUserId()

    suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?>

    suspend fun getManagersList(token: String): Either<Exception, List<ManagersModel>>
    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>
    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?>
}