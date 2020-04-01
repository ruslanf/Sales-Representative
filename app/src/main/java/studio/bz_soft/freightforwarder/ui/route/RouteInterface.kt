package studio.bz_soft.freightforwarder.ui.route

interface RouteInterface {
    fun setWorkStarted(isStarted: Boolean)
    fun getWorkStarted(): Boolean?

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