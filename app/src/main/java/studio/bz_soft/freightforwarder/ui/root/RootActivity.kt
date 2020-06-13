package studio.bz_soft.freightforwarder.ui.root

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.PERMISSION_REQUEST_LOCATION
import studio.bz_soft.freightforwarder.root.Constants.PERMISSION_REQUEST_STORAGE
import studio.bz_soft.freightforwarder.root.Constants.SERVICE_GPS_BROADCAST
import studio.bz_soft.freightforwarder.root.Constants.SERVICE_GPS_MESSAGE
import studio.bz_soft.freightforwarder.root.service.LocationReceiver
import studio.bz_soft.freightforwarder.root.service.LocationService
import studio.bz_soft.freightforwarder.root.showError
import studio.bz_soft.freightforwarder.root.showToast
import studio.bz_soft.freightforwarder.ui.auth.AuthActivity
import kotlin.coroutines.CoroutineContext

class RootActivity : AppCompatActivity(), CoroutineScope {

    private val logTag = RootActivity::class.java.simpleName

    private val controller by inject<RootController>()

    private var locationManager : LocationManager? = null

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    private val locationReceiver = LocationReceiver()
    private var token = EMPTY_STRING
    private var ex: Exception? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setSupportActionBar(toolbar)

        initialize()

        controller.getUserToken()?.let { token = it }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menuWorkShift, R.id.menuStores, R.id.menuProfile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        mainBottomNavigationMenu.setupWithNavController(navController)
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        checkLocationPermission()
        checkStoragePermission()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager
            .getInstance(this)
            .unregisterReceiver(locationReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationService.stopService(this)
        locationManager = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        if (menu is MenuBuilder) menu.setOptionalIconsVisible(true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        NavigationUI.onNavDestinationSelected(item, navController)
        when (item.itemId) {
            R.id.menuLogout -> exitButtonListener()
            R.id.menuSync -> syncButtonListener()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_LOCATION -> {
                if (grantResults.size == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startService()
                } else showToast(this, getString(R.string.gps_service_error_message_no_location_permission))
            }
        }
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestStoragePermission()
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE)
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestLocationPermission() else startService()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_LOCATION)
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_LOCATION)
        }
    }

    private fun initialize() {
        locationManager?.let {  } ?: run {
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        checkLocationEnabled()
    }

    private fun checkLocationEnabled() {
        locationManager?.let {
            val isGpsEnabled = it.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.gps_service_open_location_service_gps_error_title))
                    .setPositiveButton(getString(R.string.gps_service_open_location_service_message)) { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton(getString(R.string.gps_service_open_location_service_negative_button), null)
                    .show()
            }
        }
    }

    private fun startService() {
        LocationService.startService(this, SERVICE_GPS_MESSAGE)
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(locationReceiver, IntentFilter(SERVICE_GPS_BROADCAST))
    }

    private fun exitButtonListener() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val alertDialog = AlertDialog.Builder(this).create()
        with(alertDialog) {
            setView(dialogView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogView.exitButton.setOnClickListener { dialogExitButtonListener(this) }
            dialogView.cancelButton.setOnClickListener { dismiss() }
            show()
        }
    }

    private fun dialogExitButtonListener(alertDialog: AlertDialog?) {
        alertDialog?.dismiss()
        progressBar.visibility = View.VISIBLE
        controller.deleteToken()
        controller.deleteUserId()
        progressBar.visibility = View.GONE
        startActivity(Intent(this@RootActivity, AuthActivity::class.java))
    }

    private fun syncButtonListener() {
        progressBar.visibility = View.VISIBLE
        val points = mutableListOf<StorePointModel>()
        launch {
            coroutineScope {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    val list = controller.getAllFromTradePoint()
                    list.forEach {  tp ->
                        points.add(StorePointModel(getUserId(), tp.workShift, tp.storePoint,
                            tp.type, tp.taxNumber, tp.taxNumber_1,
                            tp.addressActual, tp.latitude, tp.longitude, tp.addressLegal, tp.phone,
                            tp.email, tp.lprName, tp.paymentType, tp.productsRange, tp.marketType,
                            tp.companyType, tp.workTime, tp.dealer, tp.note,  tp.photoOutside, tp.photoInside,
                            tp.photoGoods, tp.photoCorner)
                        )
                    }
                    when (val r = controller.syncTradePoint(token, points)) {
                        is Right -> {  }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
            }
            progressBar.visibility = View.GONE
            ex?.let {
                showError(this@RootActivity, it, R.string.root_sync_message_error, logTag)
            } ?: run {

            }
        }
    }

    private fun getUserId(): Int = controller.getUserId() ?: 0
}
