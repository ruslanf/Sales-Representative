package studio.bz_soft.freightforwarder.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.activity_root.progressBar
import kotlinx.android.synthetic.main.dialog_birthday.view.*
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import kotlinx.android.synthetic.main.dialog_change_password.view.changeButton
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.managerSpinner
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.ManagersModel
import studio.bz_soft.freightforwarder.data.models.Passwords
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.root.*
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.extensions.Sex
import studio.bz_soft.freightforwarder.ui.auth.AuthActivity
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import java.util.*
import kotlin.coroutines.CoroutineContext

class ProfileFragment : Fragment(), CoroutineScope {

    private val logTag = ProfileFragment::class.java.simpleName

    private val presenter by inject<ProfilePresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token: String = EMPTY_STRING
    private var userProfile: UserProfileModel? = null
    private var managersList: List<ManagersModel>? = null
    private lateinit var managers: Array<String>
    private var manager = EMPTY_STRING
    private var ex: Exception? = null
    private var exUP: Exception? = null
    private var exLM: Exception? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.getUserToken()?.let { token = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            loadProfileInfo(this)

            userPasswordTV.setOnClickListener { changePasswordListener(this) }
            userBirthdayTV.setOnClickListener { birthdayListener(this) }
            sexRG.setOnCheckedChangeListener { _, checkedId -> radioGroupListener(checkedId) }

            logoutButton.setOnClickListener { exitButtonListener() }
        }
    }

    override fun onPause() {
        super.onPause()
        view?.let { updateProfile(it) }
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.VISIBLE
    }

    private fun loadProfileInfo(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            launch {
                coroutineScope {
                    val up = async(SupervisorJob(job) + Dispatchers.IO) {
                        when (val r = presenter.loadUserProfile(token)) {
                            is Right -> { userProfile = r.value }
                            is Left -> { exUP = r.value }
                        }
                    }
                    val ml = async(SupervisorJob(job) + Dispatchers.IO) {
                        when (val r = presenter.getManagersList(token)) {
                            is Right -> { managersList = r.value }
                            is Left -> { exLM = r.value }
                        }
                    }
                    up.await()
                    ml.await()
                }
                exUP?.let {
                    showError(context, it, R.string.profile_load_data_message_error, logTag)
                } ?: run {
                    userProfile?.let {
                        val name = if (it.middleName.isNullOrBlank())
                            "${it.firstName} ${it.lastName}"
                        else
                            "${it.firstName} ${it.middleName} ${it.lastName}"
                        userNameET.setText(name, TextView.BufferType.EDITABLE)
                        userEmailET.setText(it.email)
                        userPasswordTV.setText(R.string.profile_password_view)
                        userBirthdayTV.text = it.birthday
                        setRadioGroup(it.gender.toString().toUpperCase())
                        phoneET.setText(it.phone)
                        addressET.setText(it.address)
                        it.manager?.let { m -> manager = m }
                    }
                }

                exLM?.let {
                    showError(context, it, R.string.profile_load_data_mangers_error, logTag)
                } ?: run {
                    managersList?.let { m ->
                        managers = Array(m.size) { "" }
                        var i = 0
                        m.forEach { managers[i++] = it.manager!! }
                        managerSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_end, managers)
                    }
                }

                try {
                    if (manager.isNotEmpty() && managers.isNotEmpty())
                        managerSpinner.setSelection(managers.indexOf(manager))
                } catch (e: UninitializedPropertyAccessException) {
                    showError(context, e, R.string.profile_load_data_mangers_error, logTag)
                }
                progressBar.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NewApi")
    private fun updateProfile(v: View): Boolean {
        var result = false
        v.apply {
            progressBar.visibility = View.VISIBLE
            try {
                var userId = 0
                userProfile?.userId?.let { userId = it }
                val name = userNameET.text.toString().split(" ").toTypedArray()
                val firstName: String
                val middleName: String
                val lastName: String
                if (name.size == 3) {
                    firstName = name[0]
                    middleName = name[1]
                    lastName = name[2]
                } else {
                    firstName = name[0]
                    middleName = ""
                    lastName = name[1]
                }
                val birthday = try {
//                    formattedDate(parseDate(userBirthdayTV.text.toString()))
                    formattedOutputDate(parseOutputDate(userBirthdayTV.text.toString()))
                } catch (ex: Exception) {
                    "2020-02-02"
                }
                val manager = managerSpinner.selectedItem.toString()
                launch {
                    val request = async(SupervisorJob(job) + Dispatchers.IO) {
                        when (val r = presenter.updateProfile(
                            token,
                            UserProfileModel(
                                userId,
                                userEmailET.text.toString(),
                                firstName,
                                middleName,
                                lastName,
                                birthday,
                                radioGroupListener(sexRG.checkedRadioButtonId).name,
                                phoneET.text.toString(),
                                addressET.text.toString(),
                                manager
                            )
                        )) {
                            is Right -> {  }
                            is Left -> { ex = r.value }
                        }
                    }
                    request.await()
                    progressBar.visibility = View.GONE
                    ex?.let {
                        showError(context, it, R.string.profile_update_data_message_error, logTag)
                    } ?: run {
                        result = true
                    }
                }
            } catch (ex: Exception) {
                progressBar.visibility = View.GONE
                result = false
            }
        }
        return result
    }

    private fun radioGroupListener(checkedId: Int): Sex =
        when (checkedId) {
            R.id.sexMRB -> { Sex.MALE }
            R.id.sexFRB -> { Sex.FEMALE }
            else -> Sex.MALE
        }

    private fun setRadioGroup(sex: String) {
        when (sex) {
            Sex.MALE.name -> { sexMRB.isChecked = true }
            Sex.FEMALE.name -> { sexFRB.isChecked = true }
        }
    }

    private fun changePasswordListener(v: View) {
        v.apply {
            val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
            val alertDialog = AlertDialog.Builder(context).create()
            with(alertDialog) {
                setView(dialogView)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogView.changeButton.setOnClickListener {
                    changeButtonListener(this@apply, this,
                        dialogView.dialogOldPasswordET.text.toString(),
                        dialogView.dialogNewPasswordET.text.toString())
                }
                show()
            }
        }
    }

    private fun changeButtonListener(v: View, alertDialog: AlertDialog?, oldPassword: String, newPassword: String) {
        v.apply {
            alertDialog?.dismiss()
            progressBar.visibility = View.VISIBLE
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.changePassword(token, Passwords(oldPassword, newPassword))) {
                        is Right -> {  }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(context, it, R.string.profile_change_password_error, logTag)
                } ?: run {
                    showToast(this@apply, getString(R.string.profile_change_password_successfull))
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun birthdayListener(v: View) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        v.apply {
            val dialogView = layoutInflater.inflate(R.layout.dialog_birthday, null)
            val alertDialog = AlertDialog.Builder(context).create()
            with(alertDialog) {
                setView(dialogView)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogView.daysNP.apply {
                    /*(getChildAt(dialogView.daysNP.value) as EditText).apply {
                        setTextColor(resources.getColor(R.color.colorPrimary, null))
                        textSize = 24F
                    }*/
                    gravity = Gravity.CENTER_HORIZONTAL
                    minValue = 1
                    maxValue = 31
                    value = day
                    wrapSelectorWheel = false
                    isClickable = false
                }
                dialogView.monthNP.apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    minValue = 1
                    maxValue = 12
                    value = month + 1
                    wrapSelectorWheel = false
                    isClickable = false
                }
                dialogView.yearNP.apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    minValue = 1900
                    maxValue = 2020
                    value = year - 3
                    wrapSelectorWheel = false
                    isClickable = false
                }
                dialogView.setBirthdayButton.setOnClickListener {
                    setBirthdayListener(this@apply, this,
                        dialogView.daysNP.value,
                        dialogView.monthNP.value,
                        dialogView.yearNP.value)
                }
                show()
            }
        }
    }

    private fun setBirthdayListener(v: View, alertDialog: AlertDialog?, day: Int, month: Int, year: Int) {
        v.apply {
            alertDialog?.dismiss()
            val d = if ("$day".length < 2) "0$day" else "$day"
            val m = if ("$month".length < 2) "0$month" else "$month"
            val birthday = "$d.$m.$year"
            userBirthdayTV.text = birthday
        }
    }

    // TODO Add make photo function

    private fun loadPhoto(v: View, photo: String?) {
        v.apply {
            photo?.let {
                GlideApp.with(this)
                    .load(it)
                    .placeholder(R.drawable.ic_auth_user)
                    .transform(CircleCrop())
                    .into(userPhotoIV)
            }
        }
    }

    private fun exitButtonListener() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val alertDialog = AlertDialog.Builder(context).create()
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
        presenter.deleteToken()
        presenter.deleteUserId()
        progressBar.visibility = View.GONE
        startActivity(Intent(context, AuthActivity::class.java))
    }

    private fun getUserId(): Int = presenter.getUserId() ?: run { 0 }

    companion object {
        fun instance() = ProfileFragment()
    }
}