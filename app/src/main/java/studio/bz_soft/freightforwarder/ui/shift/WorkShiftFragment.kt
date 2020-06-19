package studio.bz_soft.freightforwarder.ui.shift

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.dialog_work_shift_started_finished.view.*
import kotlinx.android.synthetic.main.fragment_work_shift.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import retrofit2.HttpException
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.DistanceModel
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.data.models.db.Distance
import studio.bz_soft.freightforwarder.data.models.db.WorkShift
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.showError
import studio.bz_soft.freightforwarder.root.showToast
import studio.bz_soft.freightforwarder.ui.auth.AuthActivity
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
    private var startDate = EMPTY_STRING
    private var startTime = EMPTY_STRING
    private var workShift: WorkShift? = null
    private var distance: Distance? = null

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
                    if ((ex as HttpException).code() == 401) logout(this@apply)
                    ex = null
                } ?: run {
                    userProfile?.let {
                        userId = it.userId
                        fName = it.firstName ?: EMPTY_STRING
                        sName = it.middleName ?: EMPTY_STRING
                        lName = it.lastName ?: EMPTY_STRING
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
            timeTC.text = presenter.currentTime()
        }
    }

    private fun getCurrentDate(v: View) {
        v.apply {
            dateTV.text = presenter.currentDate()
        }
    }

    private fun workButtonListener(v: View) {
        v.apply {
            presenter.getWorkStarted()?.let {
                presenter.setWorkStarted(!it)
                workButtonState(this)
                when (it) {
                    true -> { startWorkShiftDialog(this, false) } // End Work shift
                    false -> { startWorkShiftDialog(this, true) } // Start Work shift
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

    private fun startWorkShiftDialog(v: View, isStarted: Boolean) {
        v.apply {
            layoutInflater.inflate(R.layout.dialog_work_shift_started_finished, null).also {
                with(AlertDialog.Builder(context).create()) {
                    setView(it)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    it.textView.text = resources.getString(
                        if (isStarted) R.string.fragment_work_shift_started
                        else R.string.fragment_work_shift_ended
                    )
                    it.okButton.setOnClickListener {
                        dismiss()
                        if (isStarted) startWorkShift(this@apply) else endWorkShift(this@apply)
                    }
                    it.cancelButton.setOnClickListener { dismiss() }
                    show()
                }
            }
        }
    }

    private fun startWorkShift(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            startDate = presenter.currentDate()
            startTime = presenter.currentTime()
            presenter.getUserToken()?.let { token = it }
            launch {
                coroutineScope {
                    val startRequest = async(SupervisorJob(job) + Dispatchers.IO) {
                        presenter.startWorkShift(
                            WorkShift(0, userId, fName, sName, lName,
                                startDate, EMPTY_STRING,
                                startTime, EMPTY_STRING))
                    }
                    val lastDataRequest = async(SupervisorJob(job) + Dispatchers.IO) {
                        workShift = presenter.getLastData()
                    }
                    startRequest.await()
                    lastDataRequest.await()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun endWorkShift(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            launch {
                coroutineScope {
                    val db = async(SupervisorJob(job) + Dispatchers.IO) {
                        workShift?.let {
                            presenter.updateWorkShift(presenter.currentDate(), presenter.currentTime(), it.id)
                        }
                    }
                    val lastDistance = async(SupervisorJob(job) + Dispatchers.IO) {
                        distance = presenter.getLastDistance()
                    }
                    db.await()
                    lastDistance.await()
                    ex = null
                    distance?.let {
                        val sendDistance = async(SupervisorJob(job) + Dispatchers.IO) {
                            Log.d(logTag, "Дистанция => ${distance?.distance}")
                            when (val r = presenter.sendDistance(token, DistanceModel(userId, it.workShift, it.distance))) {
                                is Right -> {  }
                                is Left -> { ex = r.value }
                            }
                        }
                        sendDistance.await()
                    }
                    progressBar.visibility = View.GONE
                    ex?.let {
                        showError(context, it, R.string.fragment_work_shift_send_distance_error, logTag)
                    } ?: run {
                        showToast(this@apply, "Дистанция => ${distance?.distance}, на сервер загружена")
                    }
                }
            }
        }
    }

    private fun logout(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            presenter.deleteToken()
            presenter.deleteUserId()
            progressBar.visibility = View.GONE
            startActivity(Intent(context, AuthActivity::class.java))
        }
    }
}