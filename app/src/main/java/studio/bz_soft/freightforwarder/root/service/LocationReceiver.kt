package studio.bz_soft.freightforwarder.root.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.LocationModel
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.root.Constants
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.formattedDate
import studio.bz_soft.freightforwarder.root.getCurrentDT
import studio.bz_soft.freightforwarder.root.parseDate
import kotlin.coroutines.CoroutineContext

class LocationReceiver : BroadcastReceiver(), CoroutineScope, KoinComponent {

    private val logTag = LocationReceiver::class.java.simpleName

    private val controller by inject<LocationController>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Constants.SERVICE_GPS_BROADCAST_RECEIVE -> {
                val longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
                val latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
                insertLocation(latitude, longitude)
                controller.getWorkStarted()?.let {
                    if (it) sendLocation(latitude, longitude)
                    if (it) setStartLocation(latitude, longitude)
                    if (it) computeDistance(latitude, longitude)
                }
            }
        }
    }

    private fun insertLocation(latitude: Double, longitude: Double) {
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                if (BuildConfig.DEBUG) Log.d(logTag, "Longitude => $longitude, Latitude => $latitude")
                controller.insertLocation(Location(0, longitude, latitude))
            }
            request.await()
        }
    }

    private fun sendLocation(latitude: Double, longitude: Double) {
        val token = getToken()
        val userId = getUserId()
        val ws = getWorkShift()
        var ex: Exception? = null
        if (token.isNotEmpty() && userId != 0)
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = controller.sendLocation(token, LocationModel(userId, ws, latitude, longitude))) {
                        is Right -> {  }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                ex?.let {
                    if (BuildConfig.DEBUG) Log.d(logTag, "Error send location -> ${ex?.message}")
                } ?: run {

                }
            }
    }

    private fun setStartLocation(latitude: Double, longitude: Double) {
        controller.setStartLatitude(latitude.toString())
        controller.setStartLongitude(longitude.toString())
    }

    private fun computeDistance(latitude: Double, longitude: Double) {
        val distanceResult = FloatArray(3)
        var startLatitude = 0.0
        var endLongitude = 0.0
        controller.getStartLatitude()?.let { startLatitude = it.toDouble() }
        controller.getStartLongitude()?.let { endLongitude = it.toDouble() }
        android.location.Location.distanceBetween(startLatitude, endLongitude, latitude, longitude, distanceResult)
        val distance = distanceResult[0]
        controller.setDistance(distance.toString())
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                if (BuildConfig.DEBUG) Log.d(logTag, "Distance => $distance")
                controller.insertDistance(Distance(0, getWorkShift(), distance))
            }
            request.await()
        }
    }

    private fun getToken(): String = controller.getUserToken() ?: EMPTY_STRING
    private fun getUserId(): Int = controller.getUserId() ?: 0
    private fun getWorkShift(): String = formattedDate(parseDate(getCurrentDT()))
}
