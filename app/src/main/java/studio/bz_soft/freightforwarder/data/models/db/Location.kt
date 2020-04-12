package studio.bz_soft.freightforwarder.data.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "latitude") val longitude: Double,
    @ColumnInfo(name = "longitude") val latitude: Double
)