package studio.bz_soft.freightforwarder.root

object Constants {
    const val API_MAIN_URL = "https://indoevropeist.site"
    const val BASE_API = "/api"

    const val PLATFORM = "android"

    const val SPLASH_TIME_OUT: Long = 2000

    const val OUT_DATE_FORMATTER = "dd.MM.yyyy"
    const val IN_DATE_FORMATTER = "yyyy-MM-dd"

    val DAYS = IntArray(31) { i -> i + 1 }
    val MONTH = IntArray(12) { i -> i + 1 }
    val YEAR = IntArray(200) { i -> 1900 + i + 1}

    const val EMPTY_STRING = ""
    const val ZERO_STRING = "0"

    const val IS_TOKEN_PRESENT = "is_user_token_present"
    const val USER_TOKEN = "user_token"
    const val USER_ID = "user_id"
}