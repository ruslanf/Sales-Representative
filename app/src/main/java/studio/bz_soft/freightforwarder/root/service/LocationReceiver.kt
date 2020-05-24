package studio.bz_soft.freightforwarder.root.service

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location.distanceBetween
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.LocationModel
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.root.Constants
import studio.bz_soft.freightforwarder.root.Constants.CHANNEL_ID
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.LOCATION
import studio.bz_soft.freightforwarder.root.Constants.NOTIFICATION_ID
import kotlin.coroutines.CoroutineContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationReceiver : BroadcastReceiver(), CoroutineScope, KoinComponent {

    private val logTag = LocationReceiver::class.java.simpleName

    private val controller by inject<LocationController>()

    private var notificationManager: NotificationManager? = null
    private lateinit var context: Context

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        when (intent.action) {
            Constants.SERVICE_GPS_BROADCAST_RECEIVE -> {
                val location = intent.getParcelableExtra<android.location.Location>(LOCATION)
                controller.getWorkStarted()?.let {
                    if (it) insertLocation(location.latitude, location.longitude)
                    if (it) sendLocation(location.latitude, location.longitude)
                    if (it) computeDistance(location)
//                    if (it) calculateDistance(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun showNotification(distance: Double) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("Distance: $distance")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun insertLocation(latitude: Double, longitude: Double) {
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                controller.insertLocation(studio.bz_soft.freightforwarder.data.models.db.Location(0, longitude, latitude))
            }
            request.await()
        }
    }

    private fun sendLocation(latitude: Double, longitude: Double) {
        val token = controller.getUserToken() ?: EMPTY_STRING
        val userId = controller.getUserId() ?: 0
        val ws = controller.getWorkShift()
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
                } ?: run {  }
            }
    }

    private fun computeDistance(loc: android.location.Location) {
        if (loc.hasAccuracy() && loc.accuracy > 20.0) {
            val distanceResult = FloatArray(3)
            if (Location.latitude != 0.0 && Location.longitude != 0.0)
                distanceBetween(
                    Location.latitude,
                    Location.longitude,
                    loc.latitude,
                    loc.longitude,
                    distanceResult
                )
            Location.latitude = loc.latitude
            Location.longitude = loc.longitude
            val distance = distanceResult[0] / 1000
            Location.distance += distance
            showNotification(Location.distance.toDouble())
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    controller.insertDistance(
                        Distance(workShift = controller.getWorkShift(), distance = Location.distance)
                    )
                }
                request.await()
            }
        }
    }

    private fun calculateDistance(latitude: Double, longitude: Double) {
        if (Location.latitude == 0.0 && Location.longitude == 0.0) {
            Location.latitude = latitude
            Location.longitude = longitude
        }
        val radius = 6371000
        val dLatitude = Math.toRadians(latitude - Location.latitude)
        val dLongitude = Math.toRadians(longitude - Location.longitude)
        val a = sin(dLatitude / 2) * sin(dLatitude / 2) +
                cos(Math.toRadians(Location.latitude)) * cos(Math.toRadians(latitude)) *
                sin(dLongitude / 2) * sin(dLongitude / 2)
        val distance = (radius * (2 * atan2(sqrt(a), sqrt(1 - a)))) / 1000
        if (Location.latitude != latitude || Location.longitude != longitude) Location.distance += distance.toFloat()
        showNotification(Location.distance.toDouble())

        Location.latitude = latitude
        Location.longitude = longitude

        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                controller.insertDistance(Distance(0, controller.getWorkShift(), Location.distance))
            }
            request.await()
        }
    }
}
