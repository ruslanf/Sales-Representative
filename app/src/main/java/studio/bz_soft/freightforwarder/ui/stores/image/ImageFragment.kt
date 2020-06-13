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
import com.bumptech.glide.Glide
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
import studio.bz_soft.freightforwarder.root.Constants.API_MAIN_URL
import studio.bz_soft.freightforwarder.root.Constants.CAMERA_REQUEST_CODE
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.FILE_PATH
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_ASSORTMENT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_CORNER
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_ASSORTMENT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_CORNER
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_IN
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_DESC_OUT
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_INSIDE
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_OUTSIDE
import studio.bz_soft.freightforwarder.root.Constants.IMAGE_SUFFIX
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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
        photoOutside?.let { showPhoto(outsidePhotoIV, "$API_MAIN_URL$it") }
        photoInside?.let { showPhoto(insidePhotoIV, "$API_MAIN_URL$it") }
        photoAssortment?.let { showPhoto(assortmentPhotoIV, "$API_MAIN_URL$it") }
        photoCorner?.let { showPhoto(cornerPhotoIV, "$API_MAIN_URL$it") }
    }

    private fun showPhoto(image: ImageView, url: String) {
        Glide.with(this).load(url).into(image)
    }

    private fun photoButtonListener(v: View, select: Int) {
        v.apply {
            selected = select
            val image = createImageFile().also {
                if (BuildConfig.DEBUG) Log.d(logTag, "File image => ${it.absolutePath}")
                imagePath = " DCIM/Camera/${fileName(it.toString())}"
            }
            val intentTakePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", image))
            }
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

    private fun createImageFile(): File =
        File.createTempFile(imageFileName(), IMAGE_SUFFIX, storagePath()).also {
            cameraFilePath = FILE_PATH.plus(it.absolutePath)
            Log.d(logTag, "createImageFile() camera file path -> $cameraFilePath")
        }

//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        val image: File = File.createTempFile(imageFileName(), IMAGE_SUFFIX, storagePath())
//        cameraFilePath = FILE_PATH.plus(image.absolutePath)
//        Log.d(logTag, "createImageFile() camera file path -> $cameraFilePath")
//        return image
////        return compressedImageFromUri(Uri.parse(cameraFilePath))
//    }

    private fun decodeImageFromFile(file: String) =
        BitmapFactory.Options().run {
//            val inputStream = BufferedInputStream(activity?.contentResolver?.openInputStream(Uri.parse(file))).apply { markSupported() }
            inJustDecodeBounds = true

//            inputStream.mark(inputStream.available())
//            BitmapFactory.decodeStream(inputStream, null, this)
//            inputStream.reset()

            BitmapFactory.decodeFile(file, this)
            inSampleSize = calculateInSampleSize(this, 640, 480)
            inJustDecodeBounds = false

            BitmapFactory.decodeFile(file, this)
        }

    private fun compressedImageFromUri(uri: Uri): File {
        Log.d(logTag, "compressedImageFromUri()...")
        val inputStream = BufferedInputStream(activity?.contentResolver?.openInputStream(uri)).apply { markSupported() }

//        val options = BitmapFactory.Options().apply {
//            inJustDecodeBounds = true
////            inSampleSize = 2
//            inPreferredConfig = Bitmap.Config.ARGB_8888
//        }
//        inputStream.mark(inputStream.available())
//        BitmapFactory.decodeStream(inputStream, null, options)
//        val imageHeight: Int = options.outHeight
//        val imageWidth: Int = options.outWidth
////        val imageType: String = options.outMimeType
//        Log.d(logTag, "Bitmap options: \n height => $imageHeight \n width => $imageWidth ")
//        inputStream.reset()
//
//        val hRatio = ceil(options.outHeight / 800f).toInt()
//        val wRatio = ceil(options.outWidth / 480f).toInt()
//
//        if (hRatio > 1 || wRatio > 1) options.inSampleSize = if (hRatio > wRatio) hRatio else wRatio
//        options.inJustDecodeBounds = false
//        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
//        inputStream.close()


//        return file

//        val orgWidth = options.outWidth
//        val orgHeight = options.outHeight
////        return if (orgWidth != -1 || orgHeight != -1) {
//            var scaling = if (orgWidth > orgHeight && orgWidth > 480f) (orgWidth / 480f).toInt()
//            else if (orgWidth < orgHeight && orgHeight > 800f) (orgHeight / 800f).toInt() else 1
//            scaling = if (scaling <= 0) 1 else scaling
//            options.inSampleSize = scaling
//            inputStream = activity?.contentResolver?.openInputStream(uri)
//            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
//            inputStream?.close()
            return compressImage(decodeFromStream(inputStream, 1280, 720))
////        } else null
    }

    private fun decodeFromStream(inputStream: BufferedInputStream, reqWidth: Int, reqHeight: Int) =
        BitmapFactory.Options().run {
            inJustDecodeBounds = true

            inputStream.mark(inputStream.available())
            BitmapFactory.decodeStream(inputStream, null, this)
            inputStream.reset()

            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false

            BitmapFactory.decodeStream(inputStream, null, this)
    }

    private fun compressImage(bitmap: Bitmap?): File = File(context?.cacheDir, "upload").also {
        val byteArrayOS = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOS)
        FileOutputStream(it).apply {
            write(byteArrayOS.toByteArray())
            flush()
            close()
        }
        Log.d(logTag, "File size => ${it.length()}")
    }
//    private fun compressImage(bitmap: Bitmap?): File {
//        val byteArrayOS = ByteArrayOutputStream()
//        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOS)
//        val file = File(context?.cacheDir, "upload")
//        FileOutputStream(file).apply {
//            write(byteArrayOS.toByteArray())
//            flush()
//            close()
//        }
////        val byteArrayIS = ByteArrayInputStream(byteArrayOS.toByteArray())
////        return BitmapFactory.decodeStream(byteArrayIS, null, null)
//        Log.d(logTag, "File size => ${file.length()}")
//        return file
//    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) inSampleSize *= 2
        }

        return inSampleSize
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