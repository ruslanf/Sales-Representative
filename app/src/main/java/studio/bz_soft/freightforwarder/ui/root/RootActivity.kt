package studio.bz_soft.freightforwarder.ui.root

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.dialog_logout.view.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.root.Constants.SERVICE_GPS_BROADCAST
import studio.bz_soft.freightforwarder.root.Constants.SERVICE_GPS_MESSAGE
import studio.bz_soft.freightforwarder.root.service.LocationReceiver
import studio.bz_soft.freightforwarder.root.service.LocationService
import studio.bz_soft.freightforwarder.ui.auth.AuthActivity

class RootActivity : AppCompatActivity() {

    private val logTag = RootActivity::class.java.simpleName

    private val controller by inject<RootController>()

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    private val locationReceiver = LocationReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setSupportActionBar(toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menuWorkShift, R.id.menuStores, R.id.menuProfile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        mainBottomNavigationMenu.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        LocationService.startService(this, SERVICE_GPS_MESSAGE)
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(locationReceiver, IntentFilter(SERVICE_GPS_BROADCAST))
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
        if (item.itemId == R.id.menuLogout) { exitButtonListener() }
        return super.onOptionsItemSelected(item)
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
}
