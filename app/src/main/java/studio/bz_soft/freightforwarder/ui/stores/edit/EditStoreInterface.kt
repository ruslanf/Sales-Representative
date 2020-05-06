package studio.bz_soft.freightforwarder.ui.stores.edit

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel

interface EditStoreInterface {
    fun getUserId(): Int?
    fun getImageOutside(): String?
    fun deleteImageOutside()
    fun getImageInside(): String?
    fun deleteImageInside()
    fun getImageAssortment(): String?
    fun deleteImageAssortment()
    fun getImageCorner(): String?
    fun deleteImageCorner()

    suspend fun getTradePoint(token: String, id: Int): Either<Exception, StorePointModel>
    suspend fun updateTradePoint(token: String, id: Int, storePoint: StorePointModel): Either<Exception, Unit?>
}