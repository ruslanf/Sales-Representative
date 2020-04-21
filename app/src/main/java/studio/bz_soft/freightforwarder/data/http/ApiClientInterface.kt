package studio.bz_soft.freightforwarder.data.http

import okhttp3.MultipartBody
import studio.bz_soft.freightforwarder.data.models.*

interface ApiClientInterface {
    suspend fun signUp(userRequest: UserRequest): AuthResponseModel
    suspend fun signIn(userRequest: UserRequest): AuthResponseModel

    suspend fun restorePassword(email: Email): Unit?
    suspend fun changePassword(token: String, passwords: Passwords): Unit?

    suspend fun getManagersList(token: String): List<ManagersModel>
    suspend fun loadUserProfile(token: String): UserProfileModel
    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Unit?

    suspend fun uploadImage(token: String, image: MultipartBody.Part): List<ImageModel>
    suspend fun saveTradePoint(token: String, storePoint: StorePointModel): Unit?
    suspend fun getTradePointList(token: String): List<StorePointModel>
    suspend fun updateTradePoint(token: String, id: Int, storePoint: StorePointModel): Unit?
}