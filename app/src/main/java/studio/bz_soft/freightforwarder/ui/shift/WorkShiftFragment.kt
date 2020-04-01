package studio.bz_soft.freightforwarder.ui.shift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_work_shift.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.showError
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import kotlin.coroutines.CoroutineContext

class WorkShiftFragment : Fragment(), CoroutineScope {

    private val logTag = WorkShiftFragment::class.java.simpleName

    private val presenter by inject<WorkShiftPresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token: String = ""
    private var userProfile: UserProfileModel? = null
    private var ex: Exception? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_work_shift, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            loadUserProfile(this)

            workButtonState(this)
            workButton.setOnClickListener { workButtonListener(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.VISIBLE
    }

    private fun loadUserProfile(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            presenter.getUserToken()?.let { token = it }
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.loadUserProfile(token)) {
                        is Right -> { userProfile = r.value }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(context, it, R.string.profile_load_data_message_error, logTag)
                } ?: run {
                    userProfile?.let {
                        lNameTV.text = it.lastName?.let { l -> l } ?: EMPTY_STRING
                        fNameTV.text = it.firstName?.let { f -> f } ?: EMPTY_STRING
                        sNameTV.text = it.middleName?.let { m -> m } ?: EMPTY_STRING
                    }
                }
            }
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
}