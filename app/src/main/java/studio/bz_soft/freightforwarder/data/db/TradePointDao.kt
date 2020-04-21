package studio.bz_soft.freightforwarder.data.db

import androidx.room.*
import studio.bz_soft.freightforwarder.data.models.db.TradePoint

@Dao
interface TradePointDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tradePoint: TradePoint)

    @Update
    suspend fun update(tradePoint: TradePoint)

    @Delete
    suspend fun delete(tradePoint: TradePoint)

    @Query("Select * from trade_point")
    suspend fun getAllFromTradePoint(): List<TradePoint>

    @Query("Select * from trade_point where id = :recordId")
    suspend fun getTradePointById(recordId: Int): TradePoint
}