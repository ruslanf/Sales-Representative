package studio.bz_soft.freightforwarder.root

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import retrofit2.HttpException
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.parseError
import studio.bz_soft.freightforwarder.data.http.parseHttpError
import java.io.IOException
import java.net.URL

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

//fun parseChronometer(c: Context, milliseconds: Long): String =
//    c.getString(R.string.chronometer_time).format(milliseconds / 1000 / 60, milliseconds / 1000 % 60)