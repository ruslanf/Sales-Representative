package studio.bz_soft.freightforwarder.ui.stores

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.Location

interface StoresInterface {
    fun getUserToken(): String?
    fun getUserId(): Int?
    fun getWorkStarted(): Boolean?

    fun getWorkShift(): String
    fun currentDate(): String

    suspend fun getLastLocation(): Location
    suspend fun getTradePointList(token: String): Either<Exception, List<StorePointModel>>
}