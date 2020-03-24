package studio.bz_soft.freightforwarder.data.repository

import android.content.SharedPreferences
import studio.bz_soft.freightforwarder.root.Constants.IS_WORK_STARTED
import studio.bz_soft.freightforwarder.root.Constants.LATITUDE
import studio.bz_soft.freightforwarder.root.Constants.LONGITUDE
import studio.bz_soft.freightforwarder.root.Constants.ORIENTATION
import studio.bz_soft.freightforwarder.root.Constants.TILE_SOURCE
import studio.bz_soft.freightforwarder.root.Constants.ZOOM_LEVEL
import studio.bz_soft.freightforwarder.root.Constants.USER_ID
import studio.bz_soft.freightforwarder.root.Constants.USER_TOKEN

class LocalStorage(
    private val preferences: SharedPreferences
) : LocalStorageInterface {

    override fun setUserToken(userToken: String) {
        preferences.edit().putString(USER_TOKEN, userToken).apply()
    }

    override fun getUserToken(): String? = preferences.getString(USER_TOKEN, null)

    override fun deleteToken() {
        preferences.edit().remove(USER_TOKEN).apply()
    }

    override fun setUserId(id: Int) {
        preferences.edit().putInt(USER_ID, id).apply()
    }

    override fun getUserId(): Int? = preferences.getInt(USER_ID, 0)

    override fun deleteUserId() {
        preferences.edit().remove(USER_ID).apply()
    }

    override fun setWorkStarted(isStarted: Boolean) {
        preferences.edit().putBoolean(IS_WORK_STARTED, isStarted).apply()
    }

    override fun getWorkStarted(): Boolean? = preferences.getBoolean(IS_WORK_STARTED, false)

    override fun setTileSource(tileSource: String) {
        preferences.edit().putString(TILE_SOURCE, tileSource).apply()
    }

    override fun getTileSource(): String? = preferences.getString(TILE_SOURCE, null)

    override fun setOrientation(orientation: Float) {
        preferences.edit().putFloat(ORIENTATION, orientation).apply()
    }

    override fun getOrientation(): Float? = preferences.getFloat(ORIENTATION, 0F)

    override fun setLatitude(latitude: String) {
        preferences.edit().putString(LATITUDE, latitude).apply()
    }

    override fun getLatitude(): String? = preferences.getString(LATITUDE, null)

    override fun setLongitude(longitude: String) {
        preferences.edit().putString(LONGITUDE, longitude).apply()
    }

    override fun getLongitude(): String? = preferences.getString(LONGITUDE, null)

    override fun setZoomLevel(zoomLevel: Float) {
        preferences.edit().putFloat(ZOOM_LEVEL, zoomLevel).apply()
    }

    override fun getZoomLevel(): Float? = preferences.getFloat(ZOOM_LEVEL, 0F)
}