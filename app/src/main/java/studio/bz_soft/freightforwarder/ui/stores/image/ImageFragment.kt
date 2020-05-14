package studio.bz_soft.freightforwarder.ui.stores.image

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.ImageModel
import studio.bz_soft.freightforwarder.root.*
import studio.bz_soft.freightforwarder.root.Constants.CAMERA_REQUEST_CODE
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.FILE_PATH
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_ASSORTMENT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_CORNER
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_IN
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_OUT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_SUFFIX
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
    private var cameraFilePath: String = EMPTY_STRING
    private var selected: Int = 0
    private var isOutside = false
    private var isInside = false
    private var isAssortment = false
    private var isCorner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.getUserToken()?.let { token = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {

            outsideIV.setOnClickListener { photoButtonListener(this, 0) }
            insideIV.setOnClickListener { photoButtonListener(this, 1) }
            assortmentIV.setOnClickListener { photoButtonListener(this, 2) }
            cornerIV.setOnClickListener { photoButtonListener(this, 3) }

            addPhotoButton.setOnClickListener { finishButtonListener(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.GONE
    }

    private fun photoButtonListener(v: View, select: Int) {
        v.apply {
            selected = select
            val intentTakePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val image = createImageFile()
            if (BuildConfig.DEBUG) Log.d(logTag, "File image => ${image.absolutePath}")
            imagePath = "DCIM/Camera/${fileName(image.toString())}"
            intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", image))
            startActivityForResult(intentTakePicture, CAMERA_REQUEST_CODE)
        }
    }

    private fun finishButtonListener(v: View) {
        v.apply {
            findNavController().navigateUp()
//            if (isOutside && isInside && isAssortment && isCorner) findNavController().navigateUp()
//            else showToast(this, getString(R.string.fragment_image_error))
        }
    }

    private fun uploadPhoto(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
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
                    imageModel?.forEach { image ->
                        image.imageURL?.let {
                            when (selected) {
                                IMAGE_DESC_OUT -> {
                                    presenter.setImageOutside(it)
                                    isOutside = true
                                }
                                IMAGE_DESC_IN -> {
                                    presenter.setImageInside(it)
                                    isInside = true
                                }
                                IMAGE_DESC_ASSORTMENT -> {
                                    presenter.setImageAssortment(it)
                                    isAssortment = true
                                }
                                IMAGE_DESC_CORNER -> {
                                    presenter.setImageCorner(it)
                                    isCorner = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                when (selected) {
                    IMAGE_DESC_OUT -> {
                        outsidePhotoIV.setImageURI(Uri.parse(cameraFilePath))
                        view?.let {
                            uploadPhoto(it)
                            setCorrectIcon(it, outsideCorrectIV, true)
                        }
                    }
                    IMAGE_DESC_IN -> {
                        insidePhotoIV.setImageURI(Uri.parse(cameraFilePath))
                        view?.let {
                            uploadPhoto(it)
                            setCorrectIcon(it, insideCorrectIV, true)
                        }
                    }
                    IMAGE_DESC_ASSORTMENT -> {
                        assortmentPhotoIV.setImageURI(Uri.parse(cameraFilePath))
                        view?.let {
                            uploadPhoto(it)
                            setCorrectIcon(it, assortmentCorrectIV, true)
                        }
                    }
                    IMAGE_DESC_CORNER -> {
                        cornerPhotoIV.setImageURI(Uri.parse(cameraFilePath))
                        view?.let {
                            uploadPhoto(it)
                            setCorrectIcon(it, cornerCorrectIV, true)
                        }
                    }
                }
            }
            1 -> {  }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val image: File = File.createTempFile(imageFileName(), IMAGE_SUFFIX, storagePath())
        cameraFilePath = FILE_PATH.plus(image.absolutePath)
        return image
    }

    private fun compressedImageFromUri(uri: Uri): File {
        var inputStream = activity?.contentResolver?.openInputStream(uri)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 2
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()
        val orgWidth = options.outWidth
        val orgHeight = options.outHeight

//        return if (orgWidth != -1 || orgHeight != -1) {
            var scaling = if (orgWidth > orgHeight && orgWidth > 480f) (orgWidth / 480f).toInt()
            else if (orgWidth < orgHeight && orgHeight > 800f) (orgHeight / 800f).toInt() else 1
            scaling = if (scaling <= 0) 1 else scaling
            options.inSampleSize = scaling
            inputStream = activity?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            return compressImage(bitmap)
//        } else null
    }

    private fun compressImage(bitmap: Bitmap?): File {
        val byteArrayOS = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOS)
        val file = File(context?.cacheDir, "upload")
        val fileOS = FileOutputStream(file)
        fileOS.write(byteArrayOS.toByteArray())
        fileOS.flush()
        fileOS.close()
//        val byteArrayIS = ByteArrayInputStream(byteArrayOS.toByteArray())
//        return BitmapFactory.decodeStream(byteArrayIS, null, null)
        return file
    }

    private fun setCorrectIcon(v: View, image: ImageView, isCorrect: Boolean) {
        v.apply {
            image.setImageDrawable(
                drawable(this,
                    when (isCorrect) {
                        true -> R.drawable.ic_correct
                        false -> R.drawable.ic_incorrect
                    }
                )
            )
        }
    }
}