package studio.bz_soft.freightforwarder.root.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.root.Constants
import kotlin.coroutines.CoroutineContext

class LocationReceiver : BroadcastReceiver(), CoroutineScope, KoinComponent {

    private val logTag = LocationReceiver::class.java.simpleName

    private val presenter by inject<LocationPresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

        override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Constants.SERVICE_GPS_BROADCAST_RECEIVE -> {
                val longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
                val latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
                insertLocation(context, longitude, latitude)
            }
        }
    }

    private fun insertLocation(context: Context, longitude: Double, latitude: Double) {
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                if (BuildConfig.DEBUG) Log.d(logTag, "Longitude => $longitude, Latitude => $latitude")
                presenter.insertLocation(Location(0, longitude, latitude))
            }
            request.await()
        }
    }
}
