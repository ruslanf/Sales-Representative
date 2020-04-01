package studio.bz_soft.freightforwarder.ui.route

import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class RoutePresenter(
    private val repository: RepositoryInterface
) : RouteInterface {

    override fun setWorkStarted(isStarted: Boolean) {
        repository.setWorkStarted(isStarted)
    }

    override fun getWorkStarted(): Boolean? = repository.getWorkStarted()

    override fun setTileSource(tileSource: String) {
        repository.setTileSource(tileSource)
    }

    override fun getTileSource(): String? = repository.getTileSource()

    override fun setOrientation(orientation: Float) {
        repository.setOrientation(orientation)
    }

    override fun getOrientation(): Float? = repository.getOrientation()

    override fun setLatitude(latitude: String) {
        repository.setLatitude(latitude)
    }

    override fun getLatitude(): String? = repository.getLatitude()

    override fun setLongitude(longitude: String) {
        repository.setLongitude(longitude)
    }

    override fun getLongitude(): String? = repository.getLongitude()

    override fun setZoomLevel(zoomLevel: Float) {
        repository.setZoomLevel(zoomLevel)
    }

    override fun getZoomLevel(): Float? = repository.getZoomLevel()
}