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
import studio.bz_soft.freightforwarder.data.models.db.WorkShift
import studio.bz_soft.freightforwarder.root.*
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import kotlin.coroutines.CoroutineContext

class WorkShiftFragment : Fragment(), CoroutineScope {

    private val logTag = WorkShiftFragment::class.java.simpleName

    private val presenter by inject<WorkShiftPresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token: String = EMPTY_STRING
    private var userProfile: UserProfileModel? = null
    private var ex: Exception? = null
    private var userId = 0
    private var fName = EMPTY_STRING
    private var sName = EMPTY_STRING
    private var lName = EMPTY_STRING
    private var workShift: WorkShift? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_work_shift, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            loadUserProfile(this)
            getCurrentTime(this)
            getCurrentDate(this)

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
                    ex = null
                } ?: run {
                    userProfile?.let {
                        userId = it.userId
                        fName = it.firstName ?: EMPTY_STRING
                        sName = it.firstName ?: EMPTY_STRING
                        lName = it.firstName ?: EMPTY_STRING
                        lNameTV.text = it.lastName ?: EMPTY_STRING
                        fNameTV.text = it.firstName ?: EMPTY_STRING
                        sNameTV.text = it.middleName ?: EMPTY_STRING
                    }
                }
            }
        }
    }

    private fun getCurrentTime(v: View) {
        v.apply {
            timeTC.text = currentTime()
        }
    }

    private fun getCurrentDate(v: View) {
        v.apply {
            dateTV.text = currentDate()
        }
    }

    private fun workButtonListener(v: View) {
        v.apply {
            presenter.getWorkStarted()?.let {
                presenter.setWorkStarted(!it)
                workButtonState(this)
                when (it) {
                    true -> { endWorkShift() }
                    false -> {
                        startWorkShift()
                        getLastData()
                    }
                }
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

    private fun startWorkShift() {
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                presenter.startWorkShift(
                    WorkShift(0, userId, fName, sName, lName, currentDate(), EMPTY_STRING, currentTime(), EMPTY_STRING))
            }
            request.await()
        }
    }

    private fun getLastData() {
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                workShift = presenter.getLastData()
            }
            request.await()
        }
    }

    private fun endWorkShift() {
        launch {
            val request = async(SupervisorJob(job) + Dispatchers.IO) {
                workShift?.let {
                    presenter.updateWorkShift(currentDate(), currentTime(), it.id)
                }
            }
            request.await()
        }
    }

    private fun currentTime(): String =
        formattedTime(parseTime(getCurrentDT()))

    private fun currentDate(): String =
        formattedDate(parseDate(getCurrentDT()))
}