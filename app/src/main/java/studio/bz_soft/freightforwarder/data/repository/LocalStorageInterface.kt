package studio.bz_soft.freightforwarder.data.repository

interface LocalStorageInterface {
    fun setUserToken(userToken: String)
    fun getUserToken(): String?
    fun deleteToken()

    fun setUserId(id: Int)
    fun getUserId(): Int?
    fun deleteUserId()

    fun setWorkStarted(isStarted: Boolean)
    fun getWorkStarted(): Boolean?

    fun setImagesSaved(isImagesSaved: Boolean)
    fun getImagesSaved(): Boolean?
    fun setImageOutside(image: String)
    fun getImageOutside(): String?
    fun setImageInside(image: String)
    fun getImageInside(): String?
    fun setImageAssortment(image: String)
    fun getImageAssortment(): String?
    fun setImageCorner(image: String)
    fun getImageCorner(): String?

    fun setTileSource(tileSource: String)
    fun getTileSource(): String?
    fun setOrientation(orientation: Float)
    fun getOrientation(): Float?
    fun setLatitude(latitude: String)
    fun getLatitude(): String?
    fun setLongitude(longitude: String)
    fun getLongitude(): String?
    fun setZoomLevel(zoomLevel: Float)
    fun getZoomLevel(): Float?
}