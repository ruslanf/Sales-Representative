package studio.bz_soft.freightforwarder.ui.stores.image

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.ImageModel
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.fileName
import studio.bz_soft.freightforwarder.root.getRequestBody
import studio.bz_soft.freightforwarder.root.showError
import kotlin.coroutines.CoroutineContext

class ImageFragment : Fragment(), CoroutineScope {

    private val logTag = ImageFragment::class.java.simpleName

    private val presenter by inject<ImagePresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token: String = EMPTY_STRING
    private var imageModel: List<ImageModel>? = null
    private var ex: Exception? = null

    private var imagePath: String = EMPTY_STRING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {

        }
    }

    private fun uploadPhoto(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
//            val imagePath = "DCIM/Camera/IMG_20200312_120947.jpg"
            Log.d(logTag, "File name is => ${fileName(imagePath)}")
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.uploadImage(token, getRequestBody(imagePath))) {
                        is Right -> { imageModel = r.value }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(context, it, R.string.fragment_image_upload_error, logTag)
                    ex = null
                } ?: run {
                    Log.d(logTag, "Image uploaded => successfully...")
                    imageModel?.forEach { Log.d(logTag, "Image url => ${it.imageURL}") }
                }
            }
        }
    }
}