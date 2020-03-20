package studio.bz_soft.freightforwarder.data.repository

import android.content.SharedPreferences
import studio.bz_soft.freightforwarder.root.Constants.IS_WORK_STARTED
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
}