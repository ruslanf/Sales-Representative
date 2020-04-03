package studio.bz_soft.freightforwarder.data.repository

import okhttp3.MultipartBody
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

    override fun getUserId(): Int? = storage.getUserId()

    override fun deleteUserId() {
        storage.deleteUserId()
    }

    override fun setWorkStarted(isStarted: Boolean) {
        storage.setWorkStarted(isStarted)
    }

    override fun getWorkStarted(): Boolean? = storage.getWorkStarted()

    override fun setTileSource(tileSource: String) {
        storage.setTileSource(tileSource)
    }

    override fun getTileSource(): String? = storage.getTileSource()

    override fun setOrientation(orientation: Float) {
        storage.setOrientation(orientation)
    }

    override fun getOrientation(): Float? = storage.getOrientation()

    override fun setLatitude(latitude: String) {
        storage.setLatitude(latitude)
    }

    override fun getLatitude(): String? = storage.getLatitude()

    override fun setLongitude(longitude: String) {
        storage.setLongitude(longitude)
    }

    override fun getLongitude(): String? = storage.getLongitude()

    override fun setZoomLevel(zoomLevel: Float) {
        storage.setZoomLevel(zoomLevel)
    }

    override fun getZoomLevel(): Float? = storage.getZoomLevel()

    override suspend fun sendRegistrationData(userRequest: UserRequest): Either<Exception, AuthResponseModel> =
        safeRequest { client.signUp(userRequest) }

    override suspend fun sendAuthData(userRequest: UserRequest): Either<Exception, AuthResponseModel> =
        safeRequest { client.signIn(userRequest) }

    override suspend fun restorePassword(email: Email): Either<Exception, Unit?> =
        safeRequest { client.restorePassword(email) }

    override suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?> =
        safeRequest { client.changePassword(token, passwords) }

    override suspend fun getManagersList(token: String): Either<Exception, List<ManagersModel>> =
        safeRequest { client.getManagersList(token) }

    override suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel> =
        safeRequest { client.loadUserProfile(token) }

    override suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?> =
        safeRequest { client.updateProfile(token, userProfile) }

    override suspend fun uploadImage(token: String, image: MultipartBody.Part): Either<Exception, List<ImageModel>> =
        safeRequest { client.uploadImage(token, image) }
}