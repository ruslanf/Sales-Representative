package studio.bz_soft.freightforwarder.ui.stores

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_stores.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.Location
import studio.bz_soft.freightforwarder.root.*
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.KEY_LATITUDE
import studio.bz_soft.freightforwarder.root.Constants.KEY_LONGITUDE
import studio.bz_soft.freightforwarder.root.Constants.KEY_TOKEN
import studio.bz_soft.freightforwarder.root.Constants.KEY_TRADE_POINT_ID
import studio.bz_soft.freightforwarder.root.Constants.KEY_WORK_STARTED
import studio.bz_soft.freightforwarder.root.delegated.DelegateAdapter
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import kotlin.coroutines.CoroutineContext

class StoresFragment : Fragment(), CoroutineScope {

    private val presenter by inject<StoresPresenter>()

    private val logTag = StoresFragment::class.java.simpleName

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token = EMPTY_STRING
    private var storePointModel: List<StorePointModel>? = null
    private var ex: Exception? = null

    private val tradePointAdapter = DelegateAdapter(TradePointItemDelegate { tradePoint ->
        val bundle = bundleOf(
            KEY_TRADE_POINT_ID to tradePoint.id,
            KEY_TOKEN to token
        )
        view?.findNavController()?.navigate(R.id.editStoreFragment, bundle)
    })

    private var recyclerViewState: Parcelable? = null
    private var position = 0

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
        presenter.getUserToken()?.let { token = it }
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
            loadTradePoints(this)

            tradePointRV.apply {
                adapter = tradePointAdapter
                layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
                recyclerViewState?.apply {
                    layoutManager?.onRestoreInstanceState(recyclerViewState)
                    scrollToPosition(tradePointRV, position)
                }
            }
            swipeRefresh.setOnRefreshListener { refreshListener(this) }

            addStoreButton.setOnClickListener { storeButtonListener(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.VISIBLE
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

    private fun loadTradePoints(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.getTradePointList(token)) {
                        is Right -> { storePointModel = r.value }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(context, it, R.string.fragment_store_error_load_trade_point, logTag)
                } ?: run {
                    storePointModel?.let { renderTradePoint(it) }
                }
            }
        }
    }

    private fun renderTradePoint(grammar: List<StorePointModel>) {
        tradePointAdapter.apply {
            items.clear()
            items.addAll(grammar.map { TradePointElement.TradePointItem(it) })
            notifyDataSetChanged()
        }
    }

    private fun refreshListener(v: View) {
        v.apply {
            loadTradePoints(this)
            swipeRefresh.isRefreshing = false
        }
    }

    private fun currentDate(): String =
        formattedDate(parseDate(getCurrentDT()))
}