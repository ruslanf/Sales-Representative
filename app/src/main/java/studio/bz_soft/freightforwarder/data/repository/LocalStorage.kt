package studio.bz_soft.freightforwarder.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import studio.bz_soft.freightforwarder.root.Constants.USER_ID
import studio.bz_soft.freightforwarder.root.Constants.USER_TOKEN

class LocalStorage(
    private val preferences: SharedPreferences
) : LocalStorageInterface {

    private val gson = Gson()

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

//    override fun setDifficultyLevel(level: DifficultyLevel) {
//        preferences.edit().putInt(DIFFICULTY_LEVEL, level.ordinal).apply()
//    }
//
//    override fun getDifficultyLevel(): DifficultyLevel? =
//        DifficultyLevel.getByValue(preferences.getInt(DIFFICULTY_LEVEL, 0))
//
//    override fun setNextLesson(isActive: Boolean) {
//        preferences.edit().putBoolean(NEXT_LESSON, isActive).apply()
//    }
//
//    override fun getNextLesson(): Boolean? = preferences.getBoolean(NEXT_LESSON, false)
//
//    override fun setSoundEffect(isActive: Boolean) {
//        preferences.edit().putBoolean(SOUND_EFFECT, isActive).apply()
//    }
//
//    override fun getSoundEffect(): Boolean? = preferences.getBoolean(SOUND_EFFECT, false)
//
//    override fun setReminderMorning(isActive: Boolean) {
//        preferences.edit().putBoolean(REMINDER_MORNING, isActive).apply()
//    }
//
//    override fun getReminderMorning(): Boolean?  = preferences.getBoolean(REMINDER_MORNING, false)
//
//    override fun setReminderDay(isActive: Boolean) {
//        preferences.edit().putBoolean(REMINDER_DAY, isActive).apply()
//    }
//
//    override fun getReminderDay(): Boolean?  = preferences.getBoolean(REMINDER_DAY, false)
//
//    override fun setReminderEvening(isActive: Boolean) {
//        preferences.edit().putBoolean(REMINDER_EVENING, isActive).apply()
//    }
//
//    override fun getReminderEvening(): Boolean?  = preferences.getBoolean(REMINDER_EVENING, false)
//
//    override fun setReminderNight(isActive: Boolean) {
//        preferences.edit().putBoolean(REMINDER_NIGHT, isActive).apply()
//    }
//
//    override fun getReminderNight(): Boolean?  = preferences.getBoolean(REMINDER_NIGHT, false)
}