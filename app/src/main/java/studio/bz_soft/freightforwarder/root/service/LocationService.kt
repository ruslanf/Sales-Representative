package studio.bz_soft.freightforwarder.root.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.core.content.ContextCompat
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.root.Constants
import studio.bz_soft.freightforwarder.root.Constants.CHANNEL_ID
import studio.bz_soft.freightforwarder.root.Constants.CHANNEL_NAME
import studio.bz_soft.freightforwarder.root.Constants.LOCATION
import studio.bz_soft.freightforwarder.root.Constants.MIN_DISTANCE
import studio.bz_soft.freightforwarder.root.Constants.MIN_TIME
import studio.bz_soft.freightforwarder.root.Constants.NOTIFICATION_ID
import studio.bz_soft.freightforwarder.root.Constants.SERVICE_INTENT_MESSAGE
import studio.bz_soft.freightforwarder.root.showToast

class LocationService : Service() {

    private val logTag = LocationService::class.java.simpleName
    private var notificationManager: NotificationManager? = null

    inner class LocationListener(provider: String) : android.location.LocationListener {

        private var lastLocation: Location = Location(provider)

        override fun onLocationChanged(location: Location?) {
            if (BuildConfig.DEBUG) Log.d(logTag, "Latitude => ${location?.latitude}, Longitude => ${location?.longitude}")
            lastLocation.set(location)
            location?.let { sendBroadcast(it) }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            if (BuildConfig.DEBUG) Log.d(logTag, "onStatusChanged($provider), status => $status")
        }

        override fun onProviderEnabled(provider: String?) {
            if (BuildConfig.DEBUG) Log.d(logTag, "onProviderEnabled($provider)")
        }

        override fun onProviderDisabled(provider: String?) {
            if (BuildConfig.DEBUG) Log.d(logTag, "onProviderDisabled($provider)")
        }
    }

    private var locationManager : LocationManager? = null
    private var locationListeners = arrayOf(
        LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER),
        LocationListener(LocationManager.PASSIVE_PROVIDER)
    )

    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (BuildConfig.DEBUG) Log.d(logTag, "Service started...")
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            showToast(this, getString(R.string.gps_service_error_message_no_location_permission))
        else {
            locationManager?.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListeners[2])
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListeners[1])
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListeners[0])
//            val criteria = Criteria()
//            criteria.accuracy = Criteria.ACCURACY_FINE
//            val provider = locationManager?.getBestProvider(criteria, true)
//            locationManager?.getLastKnownLocation(provider)
        }
        startForeground()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager?.let { lm ->
            locationListeners.forEach { lm.removeUpdates(it) }
        }
    }

    private fun initialize() {
        locationManager?.let {  } ?: run {
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun sendBroadcast(location: Location) {
        val intent = Intent(Constants.SERVICE_GPS_BROADCAST_RECEIVE).apply {
            putExtra(LOCATION, location)
        }
        sendBroadcast(intent)
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel()
            else CHANNEL_ID

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = CHANNEL_ID
        val chan = NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE).apply {
            lightColor = Color.BLUE
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.createNotificationChannel(chan)
        return channelId
    }

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, LocationService::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(SERVICE_INTENT_MESSAGE, message)
            }
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, LocationService::class.java))
        }
    }
}