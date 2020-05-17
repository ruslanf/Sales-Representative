package studio.bz_soft.freightforwarder.data.repository

import okhttp3.MultipartBody
import org.koin.core.KoinComponent
import studio.bz_soft.freightforwarder.data.http.ApiClientInterface
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.http.safeRequest
import studio.bz_soft.freightforwarder.data.models.*
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

class Repository(
    private val database: DatabaseRepositoryInterface,
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

    override fun setImageOutside(image: String) {
        storage.setImageOutside(image)
    }

    override fun getImageOutside(): String? = storage.getImageOutside()

    override fun deleteImageOutside() {
        storage.deleteImageOutside()
    }

    override fun setImageInside(image: String) {
        storage.setImageInside(image)
    }

    override fun getImageInside(): String? = storage.getImageInside()

    override fun deleteImageInside() {
        storage.deleteImageInside()
    }

    override fun setImageAssortment(image: String) {
        storage.setImageAssortment(image)
    }

    override fun getImageAssortment(): String? = storage.getImageAssortment()

    override fun deleteImageAssortment() {
        storage.deleteImageAssortment()
    }

    override fun setImageCorner(image: String) {
        storage.setImageCorner(image)
    }

    override fun getImageCorner(): String? = storage.getImageCorner()

    override fun deleteImageCorner() {
        storage.deleteImageCorner()
    }

    override fun setLatitude(latitude: String) {
        storage.setLatitude(latitude)
    }

    override fun getLatitude(): String? = storage.getLatitude()

    override fun setLongitude(longitude: String) {
        storage.setLongitude(longitude)
    }

    override fun getLongitude(): String? = storage.getLongitude()

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

    override suspend fun sendLocation(token: String, location: LocationModel): Either<Exception, Unit?> =
        safeRequest { client.sendLocation(token, location) }

    override suspend fun sendDistance(token: String, distance: DistanceModel): Either<Exception, Unit?> =
        safeRequest { client.sendDistance(token, distance) }

    override suspend fun saveTradePoint(token: String, storePoint: StorePointModel): Either<Exception, Unit?> =
        safeRequest { client.saveTradePoint(token, storePoint) }

    override suspend fun getTradePointList(token: String): Either<Exception, List<StorePointModel>> =
        safeRequest { client.getTradePointList(token) }

    override suspend fun getTradePoint(token: String, id: Int): Either<Exception, StorePointModel> =
        safeRequest { client.getTradePoint(token, id) }

    override suspend fun updateTradePoint(token: String, id: Int, storePoint: StorePointModel): Either<Exception, Unit?> =
        safeRequest { client.updateTradePoint(token, id, storePoint) }

    override suspend fun syncTradePoint(token: String, listStorePoint: List<StorePointModel>): Either<Exception, Unit?> =
        safeRequest { client.syncTradePoint(token, listStorePoint) }

    // DB functions
    override suspend fun insertLocation(location: Location) {
        database.insertLocation(location)
    }

    override suspend fun deleteLocation(location: Location) {
        database.deleteLocation(location)
    }

    override suspend fun updateLocation(location: Location) {
        database.updateLocation(location)
    }

    override suspend fun getAllFromLocations(): List<Location> = database.getAllFromLocations()
    override suspend fun getLastLocation(): Location = database.getLastLocation()

    override suspend fun insertDistance(distance: Distance) {
        database.insertDistance(distance)
    }

    override suspend fun deleteDistance(distance: Distance) {
        database.deleteDistance(distance)
    }

    override suspend fun updateDistance(distance: Distance) {
        database.updateDistance(distance)
    }

    override suspend fun getAllFromDistance(): List<Distance> = database.getAllFromDistance()
    override suspend fun getLastDistance(): Distance = database.getLastDistance()

    override suspend fun insertTradePoint(tradePoint: TradePoint) {
        database.insertTradePoint(tradePoint)
    }

    override suspend fun deleteTradePoint(tradePoint: TradePoint) {
        database.deleteTradePoint(tradePoint)
    }

    override suspend fun updateTradePoint(tradePoint: TradePoint) {
        database.updateTradePoint(tradePoint)
    }

    override suspend fun getAllFromTradePoint(): List<TradePoint> = database.getAllFromTradePoint()
    override suspend fun getTradePointById(id: Int): TradePoint = database.getTradePointById(id)
    override suspend fun getLastRecordId(): Int = database.getLastRecordId()

    override suspend fun insertWorkShift(workShift: WorkShift) {
        database.insertWorkShift(workShift)
    }

    override suspend fun deleteWorkShift(workShift: WorkShift) {
        database.deleteWorkShift(workShift)
    }

    override suspend fun updateWorkShift(workShift: WorkShift) {
        database.updateWorkShift(workShift)
    }

    override suspend fun updateWorkShift(endDate: String, endTime: String, _id: Int) {
        database.updateWorkShift(endDate, endTime, _id)
    }

    override suspend fun getAllFromWorkShift(): List<WorkShift> = database.getAllFromWorkShift()

    override suspend fun getLastData(): WorkShift = database.getLastData()
}