package studio.bz_soft.freightforwarder.root

object Constants {
    const val API_MAIN_URL = "https://indoevropeist.site"
    const val BASE_API = "/api"

    const val CONTRIBUTOR_URL = "https://www.openstreetmap.org/copyright"

    const val PERMISSION_REQUEST_CAMERA = 2
    const val PERMISSION_REQUEST_LOCATION = 0
    const val PERMISSION_REQUEST_STORAGE = 1

    const val CHANNEL_ID = "Location_Service"
    const val CHANNEL_NAME = "Background Location service"

    const val DB_NAME = "ff_database.db"

    const val MIN_ZOOM_LEVEL: Double = 7.0
    const val MAX_ZOOM_LEVEL: Double = 19.0

    const val TILE_SOURCE = "tile_source"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val ORIENTATION = "orientation"
    const val ZOOM_LEVEL = "zoom_level"

    const val IS_IMAGES_SAVED = "is_images_saved"
    const val IMAGE_OUTSIDE = "image_outside"
    const val IMAGE_INSIDE = "image_inside"
    const val IMAGE_ASSORTMENT = "image_assortment"
    const val IMAGE_CORNER = "image_corner"

    const val GEO_POINT = "geo_point"

    const val PLATFORM = "android"

    const val SPLASH_TIME_OUT: Long = 2000

    const val OUT_DATE_FORMATTER = "dd.MM.yyyy"
    const val IN_DATE_FORMATTER = "yyyy-MM-dd"
    const val DATE_FORMATTER = "dd.MM.yyyy"
    const val WS_DATE_FORMATTER = "dd.MM.yy"
    const val TIME_FORMATTER = "HH:mm"
    const val TIME_STAMP_FORMATTER = "yyyy-MM-dd_HHmm"

    val DAYS = IntArray(31) { i -> i + 1 }
    val MONTH = IntArray(12) { i -> i + 1 }
    val YEAR = IntArray(200) { i -> 1900 + i + 1}

    const val IMAGE_SUFFIX = ".png"
    const val FILE_PATH = "file://"

    const val CAMERA_REQUEST_CODE = 0

    const val IMAGE_DESC_OUT = 0
    const val IMAGE_DESC_IN = 1
    const val IMAGE_DESC_ASSORTMENT = 2
    const val IMAGE_DESC_CORNER = 3

    const val MIN_TIME = 6 * 10 * 1000L
    const val MIN_DISTANCE = 0f

    const val SERVICE_GPS_MESSAGE = "GPS Service started..."
    const val SERVICE_GPS_BROADCAST = "broadcast.service.gps"
    const val SERVICE_GPS_BROADCAST_RECEIVE = "broadcast.service.gps.received"

    const val SERVICE_INTENT_MESSAGE = "inputServiceMessage"

    const val COUNTRY_DEFAULT = "Россия"
    const val EMPTY_STRING = ""
    const val ZERO_STRING = "0"

    const val IS_TOKEN_PRESENT = "is_user_token_present"
    const val USER_TOKEN = "user_token"
    const val USER_ID = "user_id"
    const val IS_WORK_STARTED = "is_work_started"

    const val KEY_TOKEN = "token"
    const val KEY_WORK_STARTED = "work_started"
    const val KEY_LATITUDE = "latitude"
    const val KEY_LONGITUDE = "longitude"
    const val KEY_TRADE_POINT_ID = "trade_point_id"
}