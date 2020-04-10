package studio.bz_soft.freightforwarder.root.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import studio.bz_soft.freightforwarder.root.Constants

class LocationReceiver : BroadcastReceiver() {

    private val logTag = LocationReceiver::class.java.simpleName

        override fun onReceive(context: Context, intent: Intent) {
        Log.d(logTag, "Broadcast from => ${intent.action}")
        when (intent.action) {
            Constants.SERVICE_GPS_BROADCAST_RECEIVE -> { Log.d(logTag, " Received => ${intent.getDoubleExtra(Constants.LATITUDE, 0.0) }") }
        }
    }
}
