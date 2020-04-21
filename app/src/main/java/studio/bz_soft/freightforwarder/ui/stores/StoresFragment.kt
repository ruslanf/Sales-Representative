package studio.bz_soft.freightforwarder.ui.stores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_stores.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.root.Constants.KEY_LATITUDE
import studio.bz_soft.freightforwarder.root.Constants.KEY_LONGITUDE
import studio.bz_soft.freightforwarder.root.Constants.KEY_WORK_STARTED
import studio.bz_soft.freightforwarder.root.formattedDate
import studio.bz_soft.freightforwarder.root.getCurrentDT
import studio.bz_soft.freightforwarder.root.parseDate
import studio.bz_soft.freightforwarder.root.showToast
import kotlin.coroutines.CoroutineContext

class StoresFragment : Fragment(), CoroutineScope {

    private val presenter by inject<StoresPresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var isWorkStarted = false
    private var location: Location? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.getWorkStarted()?.let {
            isWorkStarted = it
        }
        getLastLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_stores, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            getCurrentDate(this)

            addStoreButton.setOnClickListener { storeButtonListener(this) }
        }
    }

    private fun storeButtonListener(v: View) {
        v.apply {
            val bundle = bundleOf(
                KEY_WORK_STARTED to isWorkStarted,
                KEY_LATITUDE to latitude,
                KEY_LONGITUDE to longitude
            )
            if (isWorkStarted)  findNavController().navigate(R.id.addStoreFragment, bundle) else
                showToast(this, getString(R.string.fragment_stores_shift_message_error))
        }
    }

    private fun getCurrentDate(v: View) {
        v.apply {
            dateTV.text = currentDate()
        }
    }

    private fun getLastLocation() {
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                location = presenter.getLastLocation()
            }
            request.await()
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
            }
        }
    }

    private fun currentDate(): String =
        formattedDate(parseDate(getCurrentDT()))
}