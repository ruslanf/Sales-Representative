package studio.bz_soft.freightforwarder.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.data.models.db.Outlet
import studio.bz_soft.freightforwarder.root.Constants.DB_NAME

@Database(entities = [Location::class, Outlet::class], version = 1, exportSchema = false)
abstract class RoomDB : RoomDatabase() {

    abstract fun locationDao() : LocationDao
    abstract fun outletDao() : OutletDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDataBase(context: Context): RoomDB =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: createDB(context).also { INSTANCE = it }
            }

        private fun createDB(context: Context) = Room
            .databaseBuilder(context.applicationContext, RoomDB::class.java, DB_NAME)
            .build()
    }
}