package studio.bz_soft.freightforwarder.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_auth.*
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

        initButtons()
    }

    private fun initButtons() {
        signUpButton.setOnClickListener { buttonOnClickListener(signUpButton) }
        signInButton.setOnClickListener { buttonOnClickListener(signInButton) }
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
                    saveUserData(responseModel)
                    this@AuthActivity.let { activity ->
                        Intent(activity, RootActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }.also { activity.startActivity(it) }
                    }
                }
            }
        } else {
            progressBar.visibility = View.GONE
            if (BuildConfig.DEBUG) Log.d(logTag, "Not filled all requested fields")
            showToast(this, getString(R.string.auth_message_not_filled))
        }
    }

    private fun saveUserData(response: AuthResponseModel?) {
        response?.apply {
            id?.let { presenter.setUserId(it) }
            token?.let { presenter.setUserToken(it) }
        }
    }
}
