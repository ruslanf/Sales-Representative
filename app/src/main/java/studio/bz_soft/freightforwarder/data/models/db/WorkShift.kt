package studio.bz_soft.freightforwarder.data.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_shift")
class WorkShift(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "s_name") val middleName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "manager") val manager: String,
    @ColumnInfo(name = "start_shift") val start: String,
    @ColumnInfo(name = "end_shift") val end: String
)