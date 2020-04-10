package studio.bz_soft.freightforwarder.root.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.root.Constants
import studio.bz_soft.freightforwarder.root.Constants.MIN_DISTANCE
import studio.bz_soft.freightforwarder.root.Constants.MIN_TIME
import studio.bz_soft.freightforwarder.root.Constants.SERVICE_INTENT_MESSAGE
import studio.bz_soft.freightforwarder.root.showToast

class LocationService : Service() {

    private val logTag = LocationService::class.java.simpleName

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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) showToast(this, getString(R.string.gps_service_error_message_no_location_permission))

        locationManager?.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListeners[2])
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListeners[1])
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListeners[0])
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
        val intent = Intent(Constants.SERVICE_GPS_BROADCAST_RECEIVE)
        intent.putExtra(Constants.LATITUDE, location.latitude)
        intent.putExtra(Constants.LONGITUDE, location.longitude)
        sendBroadcast(intent)
    }

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, LocationService::class.java)
            startIntent.putExtra(SERVICE_INTENT_MESSAGE, message)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, LocationService::class.java))
        }
    }
}