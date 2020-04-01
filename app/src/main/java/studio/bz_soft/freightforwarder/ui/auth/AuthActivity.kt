package studio.bz_soft.freightforwarder.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.dialog_restore_password.view.*
import kotlinx.android.synthetic.main.dialog_restore_password.view.dialogEmailET
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.AuthResponseModel
import studio.bz_soft.freightforwarder.root.showError
import studio.bz_soft.freightforwarder.root.showToast
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import kotlin.coroutines.CoroutineContext

class AuthActivity : AppCompatActivity(), CoroutineScope {

    private val presenter by inject<AuthPresenter>()

    private val logTag = AuthActivity::class.java.simpleName

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var responseModel: AuthResponseModel? = null
    private var ex: Exception? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        loadToken()
        initButtons()
    }

    private fun initButtons() {
        signUpButton.setOnClickListener { buttonOnClickListener(signUpButton) }
        signInButton.setOnClickListener { buttonOnClickListener(signInButton) }
        restorePasswordLinkTV.setOnClickListener { restorePasswordListener() }
    }

    private fun buttonOnClickListener(button: Button) {
        when (button) {
            signUpButton -> { authListener(false) }
            signInButton -> { authListener(true) }
        }
    }

    private fun authListener(isRegistered: Boolean) {
        progressBar.visibility = View.VISIBLE
        val email = emailET.text
        val pass = passwordET.text
        if (email.isNotBlank() && pass.isNotBlank()) {
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = if (isRegistered) presenter.signInUser(email.toString(), pass.toString())
                    else presenter.signUpUser(email.toString(), pass.toString())) {
                        is Right -> { responseModel = r.value }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(this@AuthActivity, it, R.string.auth_message_error, logTag)
                } ?: run {
                    if (BuildConfig.DEBUG) Log.d(logTag, "response token => ${responseModel?.token}, user id => ${responseModel?.id}")
                    presenter.getUserToken()?.let {  } ?: run { responseModel?.token?.let { presenter.setUserToken(it) } }
                    saveUserData(responseModel)
                    loadApp()
                }
            }
        } else {
            progressBar.visibility = View.GONE
            if (BuildConfig.DEBUG) Log.d(logTag, "Not filled all requested fields")
            showToast(this, getString(R.string.auth_message_not_filled))
        }
    }

    private fun restorePasswordListener() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_restore_password, null)
        val alertDialog = AlertDialog.Builder(this).create()
        with(alertDialog) {
            setView(dialogView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val email = this@AuthActivity.findViewById<EditText>(R.id.emailET)
            dialogView.dialogEmailET.text = email.text
            dialogView.sendButton.setOnClickListener {
                sendButtonListener(this, dialogView.dialogEmailET.text.toString())
            }
            show()
        }
    }

    private fun sendButtonListener(alertDialog: AlertDialog?, email: String) {
            alertDialog?.dismiss()
            progressBar.visibility = View.VISIBLE
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.restorePassword(email)) {
                        is Right -> { if (BuildConfig.DEBUG) Log.d(logTag, "Password restored => ${r.value}") }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(this@AuthActivity, it, R.string.auth_message_error_pass_restore, logTag)
                } ?: run {
                    showToast(this@AuthActivity, "${getString(R.string.auth_message_pass_restored)}. ")
                }
            }
    }

    private fun saveUserData(response: AuthResponseModel?) {
        response?.apply {
            id?.let { presenter.setUserId(it) }
            token?.let { presenter.setUserToken(it) }
        }
    }

    private fun loadApp() {
        this.let { activity ->
            Intent(activity, RootActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }.also { activity.startActivity(it) }
        }
    }

    private fun loadToken() {
        presenter.getUserToken()?.let { loadApp() }
    }

    companion object {
        fun instance(): AuthActivity = AuthActivity()
    }
}
