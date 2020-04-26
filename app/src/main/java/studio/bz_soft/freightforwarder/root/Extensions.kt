package studio.bz_soft.freightforwarder.root

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import retrofit2.HttpException
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.data.http.parseError
import studio.bz_soft.freightforwarder.data.http.parseHttpError
import studio.bz_soft.freightforwarder.root.Constants.DATE_FORMATTER
import studio.bz_soft.freightforwarder.root.Constants.IN_DATE_FORMATTER
import studio.bz_soft.freightforwarder.root.Constants.OUT_DATE_FORMATTER
import studio.bz_soft.freightforwarder.root.Constants.TIME_FORMATTER
import studio.bz_soft.freightforwarder.root.Constants.TIME_STAMP_FORMATTER
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*

fun textWatcher(afterTextChanged: (String) -> Unit): TextWatcher =
    object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            afterTextChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

var EditText.value: String
    get() = this.text.toString()
    set(value) {
        this.setText(value)
    }

fun showToast(v: View, text: String) {
    Toast.makeText(v.context, biggerText(text), Toast.LENGTH_LONG).show()
}

fun showToast(c: Context, text: String) {
    Toast.makeText(c, biggerText(text), Toast.LENGTH_LONG).show()
}

private fun biggerText(text: String): String {
    val biggerText = SpannableStringBuilder(text)
    biggerText.setSpan(RelativeSizeSpan(1.35f), 0, text.length, 0)
    return biggerText.toString()
}

fun <V> ifIsNull(v: V): Boolean = v?.let { false } ?: true

fun showError(c: Context, ex: HttpException, @StringRes messageId: Int, logTag: String) {
    val error = parseHttpError(ex)
    showToast(c, "${c.getString(messageId)}. $error")
    if (BuildConfig.DEBUG) Log.d(logTag, "Error is => $error")
}

fun showError(c: Context, ex: Exception, @StringRes messageId: Int, logTag: String) {
    val error = parseError(ex)
    showToast(c, "${c.getString(messageId)}. $error")
    if (BuildConfig.DEBUG) Log.d(logTag, "Error is => $error")
}

fun drawable(v: View, @DrawableRes res: Int): Drawable = v.resources.getDrawable(res, null)

fun scrollToPosition(recyclerView: RecyclerView, position: Int) =
    Handler().postDelayed({ recyclerView.scrollToPosition(position) }, 200)

fun toUri(url: URL): Uri = Uri.parse(url.toURI().toString())

fun readJsonFromAsset(c: Context?, file: String): String? =
    try {
        c?.assets?.open(file)?.bufferedReader().use { it?.readText() }
    } catch (ex: IOException) {
        null
    }

// get DCIM path
fun storagePath(): File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera")

fun formattedTimestamp(date: LocalDateTime): String =
    date.format(ofPattern(TIME_STAMP_FORMATTER))

fun imageFileName(): String = "IMG_"
    .plus(formattedTimestamp(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toLocalDateTime()))
    .plus("_")

// get sdcard path
fun path(): String = Environment.getExternalStorageDirectory().path

fun file(file: String): File = File(path().plus("/").plus(file))

// Return file name String from path
fun fileName(file: String): String = file(file).name

fun requestBody(file: String): RequestBody = file(file).asRequestBody("multipart/form-data".toMediaTypeOrNull())

fun getRequestBody(file: String): MultipartBody.Part =
    MultipartBody.Part.createFormData("image", fileName(file), requestBody(file))

fun getCurrentDT(): String = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toString()

fun parseOutputDate(date: String): LocalDate =
    LocalDate.parse(date, ofPattern(OUT_DATE_FORMATTER).withLocale(Locale.getDefault()))

fun parseDate(date: String): LocalDate =
    LocalDate.parse(date, ISO_ZONED_DATE_TIME)

fun parseTime(time: String): LocalTime =
    LocalTime.parse(time, ISO_ZONED_DATE_TIME)

fun formattedInputDate(date: LocalDate): String =
    date.format(ofPattern(OUT_DATE_FORMATTER).withLocale(Locale.getDefault()))

fun formattedOutputDate(date: LocalDate): String =
    date.format(ofPattern(IN_DATE_FORMATTER).withLocale(Locale.getDefault()))

fun formattedTime(time: LocalTime): String =
    time.format(ofPattern(TIME_FORMATTER).withLocale(Locale.getDefault()))

fun formattedDate(date: LocalDate): String =
    date.format(ofPattern(DATE_FORMATTER))

//fun parseChronometer(c: Context, milliseconds: Long): String =
//    c.getString(R.string.chronometer_time).format(milliseconds / 1000 / 60, milliseconds / 1000 % 60)