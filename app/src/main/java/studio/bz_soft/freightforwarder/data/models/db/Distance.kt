package studio.bz_soft.freightforwarder.data.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distance")
data class Distance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "work_shift") val workShift: String,
    @ColumnInfo(name = "distance") val longitude: Double
)