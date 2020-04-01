package studio.bz_soft.freightforwarder.ui.route.store

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.dialog_add_store_et.view.*
import kotlinx.android.synthetic.main.fragment_add_store.view.*
import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.root.Constants.GEO_POINT
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import kotlin.coroutines.CoroutineContext

class AddStoreFragment : Fragment(), CoroutineScope {

    private val logTag = AddStoreFragment::class.java.simpleName

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var storeGeoPoint: GeoPoint? = null
    private var isStoreSaved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<GeoPoint>(GEO_POINT)?.let { storeGeoPoint = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_store, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            saveStoreInfoButton.setOnClickListener { saveStoreButtonListener() }

            nameOutletTV.setOnClickListener { editFieldListener(this, nameOutletTV) }
            typeTV.setOnClickListener { editFieldListener(this, typeTV) }
            taxNumberTV.setOnClickListener { editFieldListener(this, taxNumberTV) }
            taxNumber_1_TV.setOnClickListener { editFieldListener(this, taxNumber_1_TV) }
            actualAddressTV.setOnClickListener { editFieldListener(this, actualAddressTV) }
            legalAddressTV.setOnClickListener { editFieldListener(this, legalAddressTV) }
            phoneTV.setOnClickListener { editFieldListener(this, phoneTV) }
            emailTV.setOnClickListener { editFieldListener(this, emailTV) }
            lprTV.setOnClickListener { editFieldListener(this, lprTV) }
            paymentsTV.setOnClickListener { editFieldListener(this, paymentsTV) }

        }
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.GONE
        isStoreSaved = false
    }

    override fun onPause() {
        super.onPause()
        // Place check for store info saved or not
    }

    private fun saveStoreButtonListener() {
        isStoreSaved = true
    }

    private fun editFieldListener(v: View, fieldTV: TextView) {
        v.apply {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_store_et, null)
            val alertDialog = AlertDialog.Builder(context).create()
            with(alertDialog) {
                setView(dialogView)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogView.changeButton.setOnClickListener {
                    changeButtonListener(this@apply, this, fieldTV,
                        dialogView.dialogFieldET.text.toString())
                }
                show()
            }
        }
    }

    private fun changeButtonListener(v: View, alertDialog: AlertDialog?, fieldTV: TextView, field: String) {
        v.apply {
            alertDialog?.dismiss()
//            progressBar.visibility = View.VISIBLE
            when (fieldTV) {
                nameOutletTV -> { nameOutletTV.text = field }
                typeTV -> {  }
                taxNumberTV -> { taxNumberTV.text = field }
                taxNumber_1_TV -> { taxNumber_1_TV.text = field }
                actualAddressTV -> { actualAddressTV.text = field }
                legalAddressTV -> { legalAddressTV.text = field }
                phoneTV -> { phoneTV.text = field }
                emailTV -> { emailTV.text = field }
                lprTV -> { lprTV.text = field }
                paymentsTV -> { paymentsTV.text = field }
            }

//            launch {
//                val request = async(SupervisorJob(job) + Dispatchers.IO) {
//                    when (val r = presenter.changePassword(token, Passwords(oldPassword, newPassword))) {
//                        is Right -> {  }
//                        is Left -> { ex = r.value }
//                    }
//                }
//                request.await()
//                progressBar.visibility = View.GONE
//                ex?.let {
//                    showError(context, it, R.string.profile_change_password_error, logTag)
//                } ?: run {
//
//                }
//            }
        }
    }

    companion object {
        fun instance(point: GeoPoint): AddStoreFragment = AddStoreFragment().apply {
            arguments = Bundle().apply {
                putParcelable(GEO_POINT, point)
            }
        }
    }
}