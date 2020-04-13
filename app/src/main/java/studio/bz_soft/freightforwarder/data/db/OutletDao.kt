package studio.bz_soft.freightforwarder.data.db

import androidx.room.*
import studio.bz_soft.freightforwarder.data.models.db.Outlet

@Dao
interface OutletDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(outlet: Outlet)

    @Update
    suspend fun update(outlet: Outlet)

    @Delete
    suspend fun delete(outlet: Outlet)

    @Query("Select * from outlet")
    suspend fun getAllFromOutlet(): List<Outlet>
}