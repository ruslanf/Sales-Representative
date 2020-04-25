package studio.bz_soft.freightforwarder.data.repository

import okhttp3.MultipartBody
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.*
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

interface RepositoryInterface {
    fun setUserToken(userToken: String)
    fun getUserToken(): String?
    fun deleteToken()

    fun setUserId(id: Int)
    fun getUserId(): Int?
    fun deleteUserId()

    fun setWorkStarted(isStarted: Boolean)
    fun getWorkStarted(): Boolean?

    fun setImagesSaved(isImagesSaved: Boolean)
    fun getImagesSaved(): Boolean?
    fun setImageOutside(image: String)
    fun getImageOutside(): String?
    fun setImageInside(image: String)
    fun getImageInside(): String?
    fun setImageAssortment(image: String)
    fun getImageAssortment(): String?
    fun setImageCorner(image: String)
    fun getImageCorner(): String?

    fun setLatitude(latitude: String)
    fun getLatitude(): String?
    fun setLongitude(longitude: String)
    fun getLongitude(): String?

    suspend fun sendRegistrationData(userRequest: UserRequest): Either<Exception, AuthResponseModel>
    suspend fun sendAuthData(userRequest: UserRequest): Either<Exception, AuthResponseModel>

    suspend fun restorePassword(email: Email): Either<Exception, Unit?>
    suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?>

    suspend fun getManagersList(token: String): Either<Exception, List<ManagersModel>>
    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>
    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?>

    suspend fun uploadImage(token: String, image: MultipartBody.Part): Either<Exception, List<ImageModel>>
    suspend fun saveTradePoint(token: String, storePoint: StorePointModel): Either<Exception, Unit?>
    suspend fun getTradePointList(token: String): Either<Exception, List<StorePointModel>>
    suspend fun updateTradePoint(token: String, id: Int, storePoint: StorePointModel): Either<Exception, Unit?>

    // DB functions
    suspend fun insertLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun getAllFromLocations(): List<Location>
    suspend fun getLastLocation(): Location

    suspend fun insertTradePoint(tradePoint: TradePoint)
    suspend fun deleteTradePoint(tradePoint: TradePoint)
    suspend fun updateTradePoint(tradePoint: TradePoint)
    suspend fun getAllFromTradePoint(): List<TradePoint>
    suspend fun getTradePointById(id: Int): TradePoint
    suspend fun getLastRecordId(): Int

    suspend fun insertWorkShift(workShift: WorkShift)
    suspend fun deleteWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(workShift: WorkShift)
    suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int)
    suspend fun getAllFromWorkShift(): List<WorkShift>
    suspend fun getLastData(): WorkShift
}