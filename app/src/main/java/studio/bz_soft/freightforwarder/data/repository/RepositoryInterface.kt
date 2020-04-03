package studio.bz_soft.freightforwarder.data.repository

import okhttp3.MultipartBody
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.*

interface RepositoryInterface {
    fun setUserToken(userToken: String)
    fun getUserToken(): String?
    fun deleteToken()

    fun setUserId(id: Int)
    fun getUserId(): Int?
    fun deleteUserId()

    fun setWorkStarted(isStarted: Boolean)
    fun getWorkStarted(): Boolean?

    fun setTileSource(tileSource: String)
    fun getTileSource(): String?
    fun setOrientation(orientation: Float)
    fun getOrientation(): Float?
    fun setLatitude(latitude: String)
    fun getLatitude(): String?
    fun setLongitude(longitude: String)
    fun getLongitude(): String?
    fun setZoomLevel(zoomLevel: Float)
    fun getZoomLevel(): Float?

    suspend fun sendRegistrationData(userRequest: UserRequest): Either<Exception, AuthResponseModel>
    suspend fun sendAuthData(userRequest: UserRequest): Either<Exception, AuthResponseModel>

    suspend fun restorePassword(email: Email): Either<Exception, Unit?>
    suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?>

    suspend fun getManagersList(token: String): Either<Exception, List<ManagersModel>>
    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>
    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?>

    suspend fun uploadImage(token: String, image: MultipartBody.Part): Either<Exception, List<ImageModel>>
}