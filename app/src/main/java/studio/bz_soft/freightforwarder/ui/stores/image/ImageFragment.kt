package studio.bz_soft.freightforwarder.ui.stores.image

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import coil.api.load
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import pyxis.uzuki.live.mediaresizer.MediaResizer
import pyxis.uzuki.live.mediaresizer.data.ImageResizeOption
import pyxis.uzuki.live.mediaresizer.data.ResizeOption
import pyxis.uzuki.live.mediaresizer.model.ImageMode
import pyxis.uzuki.live.mediaresizer.model.MediaType
import pyxis.uzuki.live.mediaresizer.model.ScanRequest
import pyxis.uzuki.live.richutilskt.utils.toFile
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.ImageModel
import studio.bz_soft.freightforwarder.root.*
import studio.bz_soft.freightforwarder.root.Constants.API_MAIN_URL
import studio.bz_soft.freightforwarder.root.Constants.CAMERA_REQUEST_CODE
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_ASSORTMENT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_CORNER
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_ASSORTMENT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_CORNER
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_IN
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_OUT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_INSIDE
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_OUTSIDE
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import java.io.File
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class ImageFragment : Fragment(), CoroutineScope {

    private val logTag = ImageFragment::class.java.simpleName

    private val presenter by inject<ImagePresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token = EMPTY_STRING
    private var imageModel: List<ImageModel>? = null
    private var ex: Exception? = null

    private lateinit var currentPhotoPath: String
    private var resizedFilePath = EMPTY_STRING
    private var selected: Int = 0
    private var isOutside = false
    private var isInside = false
    private var isAssortment = false
    private var isCorner = false
    private var photoOutside: String? = null
    private var photoInside: String? = null
    private var photoAssortment: String? = null
    private var photoCorner: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.getUserToken()?.let { token = it }
        arguments?.apply {
            getString(IMAGE_OUTSIDE)?.let { photoOutside = it }
            getString(IMAGE_INSIDE)?.let { photoInside = it }
            getString(IMAGE_ASSORTMENT)?.let { photoAssortment = it }
            getString(IMAGE_CORNER)?.let { photoCorner = it }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            fillPhoto()

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

    private fun fillPhoto() {
        photoOutside?.let {showPhoto(outsidePhotoIV, "$API_MAIN_URL$it") }
        photoInside?.let {showPhoto(insidePhotoIV, "$API_MAIN_URL$it") }
        photoAssortment?.let {showPhoto(assortmentPhotoIV, "$API_MAIN_URL$it") }
        photoCorner?.let {showPhoto(cornerPhotoIV, "$API_MAIN_URL$it") }
    }

    private fun showPhoto(image: ImageView, url: String) {
        if (url != API_MAIN_URL) image.load("${url}?w=360")
    }

    private fun imageResize(v: View, path: String) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            val file = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/SalesRepresentative/".toFile()
            file.mkdirs()
            val imageFile = File(file, "${imageFileName()}.jpg")
            resizedFilePath = EMPTY_STRING
            resizedFilePath = imageFile.absolutePath

            val resizeOption = ImageResizeOption.Builder()
                .setImageProcessMode(ImageMode.ResizeAndCompress)
                .setImageResolution(1280, 720)
                .setBitmapFilter(false)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setCompressQuality(65)
                .setScanRequest(ScanRequest.TRUE)
                .build()

            val option = ResizeOption.Builder()
                .setMediaType(MediaType.IMAGE)
                .setImageResizeOption(resizeOption)
                .setTargetPath(path)
                .setOutputPath(imageFile.absolutePath)
                .setCallback { code, output ->
                    showToast(this, "Фото сжато - $code, $output")
                    progressBar.visibility = View.GONE
                }
                .build()

            MediaResizer.process(option)
        }
    }

    private fun photoButtonListener(v: View, select: Int) {
        v.apply {
            selected = select
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(context.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Log.e(logTag, "Error creating photo file...")
                        view?.let { showToast(it, "Error creating photo file...") }
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                    }
                }
            }
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
                    when (val r = presenter.uploadImage(token, getResizedRequestBody(resizedFilePath))) {
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
//                val thumbnail: Bitmap? = data?.extras?.get("data") as Bitmap
                view?.let {imageResize(it, currentPhotoPath) }
                when (selected) {
                    IMAGE_DESC_OUT -> {
//                        thumbnail?.let { outsidePhotoIV.setImageBitmap(it) }
                        outsidePhotoIV.setImageURI(Uri.parse(currentPhotoPath))
                        view?.let {
                            uploadPhoto(it)
                            setCorrectIcon(it, outsideCorrectIV, true)
                        }
                    }
                    IMAGE_DESC_IN -> {
//                        thumbnail?.let { insidePhotoIV.setImageBitmap(it) }
                        insidePhotoIV.setImageURI(Uri.parse(currentPhotoPath))
                        view?.let {
                            uploadPhoto(it)
                            setCorrectIcon(it, insideCorrectIV, true)
                        }
                    }
                    IMAGE_DESC_ASSORTMENT -> {
//                        thumbnail?.let { assortmentPhotoIV.setImageBitmap(it) }
                        assortmentPhotoIV.setImageURI(Uri.parse(currentPhotoPath))
                        view?.let {
                            uploadPhoto(it)
                            setCorrectIcon(it, assortmentCorrectIV, true)
                        }
                    }
                    IMAGE_DESC_CORNER -> {
//                        thumbnail?.let { cornerPhotoIV.setImageBitmap(it) }
                        cornerPhotoIV.setImageURI(Uri.parse(currentPhotoPath))
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
    private fun createImageFile(): File =
        File.createTempFile(imageFileName(), ".jpg", storagePath()).apply {
            currentPhotoPath = absolutePath
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