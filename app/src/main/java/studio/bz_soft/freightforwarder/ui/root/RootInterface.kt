package studio.bz_soft.freightforwarder.ui.root

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.TradePoint

interface RootInterface {
    fun getUserToken(): String?
    fun deleteToken()
    fun deleteUserId()
    suspend fun getAllFromTradePoint(): List<TradePoint>
    suspend fun syncTradePoint(token: String, listStorePoint: List<StorePointModel>): Either<Exception, Unit?>
}