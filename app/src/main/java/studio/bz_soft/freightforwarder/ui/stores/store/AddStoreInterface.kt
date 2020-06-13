package studio.bz_soft.freightforwarder.ui.stores.store

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.TradePoint

interface AddStoreInterface {
    fun getUserToken(): String?
    fun getUserId(): Int?
    fun getImageOutside(): String?
    fun deleteImageOutside()
    fun getImageInside(): String?
    fun deleteImageInside()
    fun getImageAssortment(): String?
    fun deleteImageAssortment()
    fun getImageCorner(): String?
    fun deleteImageCorner()
    suspend fun saveSalesPoint(token: String, storePoint: StorePointModel): Either<Exception, Unit?>
    suspend fun saveSalesPointToDB(tradePoint: TradePoint)
}