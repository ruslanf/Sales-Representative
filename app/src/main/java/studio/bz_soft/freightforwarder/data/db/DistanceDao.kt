package studio.bz_soft.freightforwarder.data.db

import androidx.room.*
import studio.bz_soft.freightforwarder.data.models.db.Distance

@Dao
interface DistanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(distance: Distance)

    @Update
    suspend fun update(distance: Distance)

    @Delete
    suspend fun delete(distance: Distance)

    @Query("Select * from distance")
    suspend fun getAllFromDistance(): List<Distance>

    @Query("Select * from distance order by id desc limit 1")
    suspend fun getLastDistance(): Distance
}