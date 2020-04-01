package studio.bz_soft.freightforwarder.ui.route

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.android.synthetic.main.fragment_route.view.*
import org.koin.android.ext.android.inject
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.root.Constants.CONTRIBUTOR_URL
import studio.bz_soft.freightforwarder.root.Constants.GEO_POINT
import studio.bz_soft.freightforwarder.root.Constants.MAX_ZOOM_LEVEL
import studio.bz_soft.freightforwarder.root.Constants.MIN_ZOOM_LEVEL
import studio.bz_soft.freightforwarder.root.Constants.PERMISSIONS_REQUEST_CODE_LOCATION
import studio.bz_soft.freightforwarder.root.Constants.PERMISSIONS_REQUEST_CODE_STORAGE
import studio.bz_soft.freightforwarder.root.drawable
import studio.bz_soft.freightforwarder.root.showToast
import studio.bz_soft.freightforwarder.ui.root.RootActivity

class RouteFragment : Fragment(), Marker.OnMarkerClickListener, MapEventsReceiver {

    private val logTag = RouteFragment::class.java.simpleName

    private val presenter by inject<RoutePresenter>()

    private var locationOverlay: MyLocationNewOverlay? = null
    private var compassOverlay: CompassOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context?.packageName //BuildConfig.APPLICATION_ID

        checkLocationPermissions()
        checkStoragePermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_route, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            workButtonState(this)
            initMap(this)

            workButton.setOnClickListener { workButtonListener(this) }
            contributorTV.setOnClickListener { contributorLinkListener(this) }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val zoomLevel = presenter.getZoomLevel()
        val orientation = presenter.getOrientation()
        val latitude = presenter.getLatitude()?.let { it.toDouble() } ?: 0.0
        val longitude = presenter.getLongitude()?.let { it.toDouble() } ?: 0.0
        map.apply {
            zoomLevel?.let { controller.setZoom(it.toDouble()) }
            orientation?.let { mapOrientation = it }
            setExpectedCenter(GeoPoint(latitude, longitude))
        }
    }

    override fun onStart() {
        super.onStart()
//        checkLocationPermissions()
//        checkStoragePermissions()

        updateMapOverlay()
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.VISIBLE
        map.onResume()
    }

    override fun onPause() {
        super.onPause()

        presenter.setTileSource(map.tileProvider.tileSource.name())
        presenter.setOrientation(map.mapOrientation)
        presenter.setLatitude(map.mapCenter.latitude.toString())
        presenter.setLongitude(map.mapCenter.longitude.toString())
        presenter.setZoomLevel(map.zoomLevelDouble.toFloat())

        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map.onDetach()
    }

    override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
        Log.d(logTag, "onMarkerClick()...")
        Log.d(logTag, "Marker id => ${marker?.id}")
        Log.d(logTag, "Marker position => ${marker?.position}")
        return true
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        Log.d(logTag, "singleTapConfirmedHelper()...")
        Log.d(logTag, "Latitude => ${p?.latitude}, Longitude => ${p?.longitude}")
        p?.let { setStoreMarker(it) }
        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        Log.d(logTag, "longPressHelper()...")
        p?.let { removeStoreMarker(it) }
        return true
    }

    private fun initMap(v: View) {
        map.apply {
//            val httpTileSource = XYTileSource("HttpMapnik",
//                0, 19, 256, ".png", arrayOf(
//                    "http://tile.openstreetmap.fr/hot/"))
//            setTileSource(httpTileSource)
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            minZoomLevel = MIN_ZOOM_LEVEL
            maxZoomLevel = MAX_ZOOM_LEVEL
            tileProvider.tileCache.protectedTileComputers.clear()
//            tileProvider.tileCache.setAutoEnsureCapacity(false)
            tileProvider.tileCache.setStressedMemory(true)

            controller.setZoom(18.0)
//            val location = setStartLocation()
//            map.overlays.add(location)
//            controller.animateTo(location.myLocation)

//            controller.animateTo(GeoPoint(47.171819, 38.749810))
//            updateMapOverlay(v)
        }
        locationOverlay = setLocationOverlay(v)
        compassOverlay = setCompassOverlay()
//        storeOverlay = Marker(map)
        activity?.runOnUiThread { locationOverlay?.let { map.controller.animateTo(it.myLocation) } }
        updateMapOverlay()
    }

    private fun updateMapOverlay() {
        map.overlays.clear()
        locationOverlay?.let { map.overlays.add(it) }
        compassOverlay?.let { map.overlays.add(it) }
//        storeOverlay?.let { map.overlays.add(it) }
        val mapEventsOverlay = MapEventsOverlay(this)
        map.overlays.add(mapEventsOverlay)
        map.invalidate()
    }

    private fun setLocationOverlay(v: View): MyLocationNewOverlay =
        MyLocationNewOverlay(GpsMyLocationProvider(activity), map).apply {
//            val icon = (drawable(v, R.drawable.map_location) as BitmapDrawable).bitmap
            this.apply {
//                setPersonIcon(icon)
//                setPersonHotspot(icon.width / 2f, icon.height.toFloat())
////                runOnFirstFix {
////                    map.overlays.clear()
////                    map.overlays.add(this)
////                    activity?.runOnUiThread { map.controller.animateTo(this.myLocation) }
////                }
                enableMyLocation()
                enableFollowLocation()
            }
        }

    private fun setStartLocation(v: View): MyLocationNewOverlay =
        MyLocationNewOverlay(GpsMyLocationProvider(activity), map).apply {
            enableFollowLocation()
            val icon = (drawable(v, R.drawable.map_location) as BitmapDrawable).bitmap
            setPersonIcon(icon)
//            setPersonHotspot(icon.width / 2f, icon.height.toFloat())
            enableMyLocation()
        }

    private fun setCompassOverlay(): CompassOverlay =
        CompassOverlay(context, map).apply {
            enableCompass()
        }

    private fun setStoreMarker(point: GeoPoint) {
        if (addStorePoint(point)) {
            val storeMarker = addMarker(point)
            map.overlayManager.add(storeMarker)
            map.overlays.add(storeMarker)
        }
    }

    private fun removeStoreMarker(point: GeoPoint) {
//        map.overlayManager.add(addMarker(point))
        map.invalidate()
    }

    private fun addMarker(point: GeoPoint): Marker =
        Marker(map).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            view?.let { icon = drawable(it, R.drawable.map_location) }
            isFlat = true
            setInfoWindow(null)
        }

    private fun storeMarkerListener(): Boolean {
        view?.findNavController()?.navigate(R.id.addStoreFragment)
        return true
    }

    // Navigate to AddStore Fragment if store added return true
    private fun addStorePoint(point: GeoPoint): Boolean {
        val bundle = bundleOf(
            GEO_POINT to point
        )
        view?.findNavController()?.navigate(R.id.addStoreFragment, bundle)
        return true
    }

    private fun workButtonState(v: View) {
        v.apply {
            presenter.getWorkStarted()?.let {
                workButton.text = when (it) {
                    true -> { resources.getString(R.string.fragment_route_work_button_end) }
                    false -> { resources.getString(R.string.fragment_route_work_button_start) }
                }
            } ?: run { workButton.text = resources.getString(R.string.fragment_route_work_button_start) }
        }
    }

    private fun workButtonListener(v: View) {
        v.apply {
            presenter.getWorkStarted()?.let {
                presenter.setWorkStarted(!it)
                workButtonState(this)
            }
        }
    }

    private fun contributorLinkListener(v: View) {
        v.apply {
            val customTabs = CustomTabsIntent.Builder().build()
            customTabs.launchUrl(context, Uri.parse(CONTRIBUTOR_URL))
        }
    }

    private fun checkStoragePermissions() {
        if (activity?.let {
                ContextCompat.checkSelfPermission(it, WRITE_EXTERNAL_STORAGE)
            } != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE_STORAGE)
    }

    private fun checkLocationPermissions() {
        if (activity?.let { ContextCompat.checkSelfPermission(it, ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    context?.let { showToast(it, getString(R.string.fragment_route_error_location)) }
            }
            PERMISSIONS_REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    context?.let { Configuration.getInstance().osmdroidTileCache = it.cacheDir }
            }
        }
    }

    companion object {
        fun instance(): RouteFragment = RouteFragment()
    }
}
