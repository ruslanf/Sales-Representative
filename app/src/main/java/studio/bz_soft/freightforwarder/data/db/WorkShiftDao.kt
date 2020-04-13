package studio.bz_soft.freightforwarder.data.db

import androidx.room.*
import studio.bz_soft.freightforwarder.data.models.db.WorkShift

@Dao
interface WorkShiftDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workShift: WorkShift)

    @Update
    suspend fun update(workShift: WorkShift)

    @Delete
    suspend fun delete(workShift: WorkShift)

    @Query("Select * from work_shift")
    suspend fun getAllFromWorkShift(): List<WorkShift>
}