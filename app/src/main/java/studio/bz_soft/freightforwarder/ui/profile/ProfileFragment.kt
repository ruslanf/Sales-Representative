package studio.bz_soft.freightforwarder.ui.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.Passwords
import studio.bz_soft.freightforwarder.data.models.UserProfileModel
import studio.bz_soft.freightforwarder.root.Constants.COUNTRY_DEFAULT
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.GlideApp
import studio.bz_soft.freightforwarder.root.showError
import kotlin.coroutines.CoroutineContext

class ProfileFragment : Fragment(), CoroutineScope {

    private val logTag = ProfileFragment::class.java.simpleName

    private val presenter by inject<ProfilePresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token: String = ""
    private var userProfile: UserProfileModel? = null
    private var ex: Exception? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            loadUserProfile(this)

            userPasswordTV.setOnClickListener { changePasswordListener(this) }
        }
    }

    override fun onPause() {
        super.onPause()
        view?.let { updateProfile(it) }
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
                        val name = if (it.middleName.isNullOrBlank())
                            "${it.firstName} ${it.lastName}"
                        else
                            "${it.firstName} ${it.middleName} ${it.lastName}"
                        userNameET.setText(name, TextView.BufferType.EDITABLE)
                        userEmailET.setText(it.email)
                        userPasswordTV.setText(R.string.profile_password_view)
                        loadPhoto(this@apply, it.photo)
                    }
                }
            }
        }
    }

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
                val photo = userProfile?.photo
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
                                null,
                                photo,
                                "20200202",
                                COUNTRY_DEFAULT,
                                EMPTY_STRING,
                                EMPTY_STRING
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
                result = false
            }
        }
        return result
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

                }
            }
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

    private fun getUserId(): Int = presenter.getUserId()?.let { it } ?: run { 0 }

    companion object {
        fun instance() = ProfileFragment()
    }
}