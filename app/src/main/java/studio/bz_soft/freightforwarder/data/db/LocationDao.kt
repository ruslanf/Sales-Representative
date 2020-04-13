package studio.bz_soft.freightforwarder.data.db

import androidx.room.*
import studio.bz_soft.freightforwarder.data.models.db.Location

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: Location)

    @Update
    suspend fun update(location: Location)

    @Delete
    suspend fun delete(location: Location)

    @Query("Select * from location")
    suspend fun getAllFromLocation(): List<Location>
}