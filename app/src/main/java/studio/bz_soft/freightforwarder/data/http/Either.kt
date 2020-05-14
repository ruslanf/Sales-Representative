package studio.bz_soft.freightforwarder.data.http

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.data.models.errors.Errors

sealed class Either<out L, out R> {
    companion object {
        inline fun <R> of(action: () -> R): Either<Exception, R> {
            return try {
                Right(action())
            } catch (ex: Exception) {
                Left(ex)
            }
        }
    }
}

data class Right<out R>(val value: R) : Either<Nothing, R>()
data class Left<out L>(val value: L) : Either<L, Nothing>()

private val gson by lazy { Gson() }

suspend fun <T> safeRequest(request: suspend () -> T): Either<Exception, T> = Either.of { request() }

fun parseHttpError(ex: HttpException): String? {
    val errorBody = ex.response()?.errorBody()
    return when (ex.code()) {
        401 -> {
            gson.fromJson(errorBody?.charStream(), JsonObject::class.java).get("error").asString
        }
        422 -> {
            gson.fromJson(errorBody?.charStream(), JsonObject::class.java).let { jsonObject ->
                val errors = jsonObject.get("errors").asJsonArray
                val e = gson.fromJson(errors, Array<Errors>::class.java)
                if (BuildConfig.DEBUG) e.forEach {
                    Log.d("Either", "size = ${e.size}, field => ${it.fieldName}, message => ${it.message}")
                }
                if (e.isNotEmpty()) e[0].message else ""
            }
        }
        else -> ""
    }
}

fun parseError(ex: Exception): String? =
    try {
        val httpException = ex as HttpException
        val errorBody = httpException.response()?.errorBody()
        when (ex.code()) {
            401 -> {
                gson.fromJson(errorBody?.charStream(), JsonObject::class.java).get("error").asString
            }
            422 -> {
                gson.fromJson(errorBody?.charStream(), JsonObject::class.java).let { jsonObject ->
                    val errors = jsonObject.get("errors").asJsonArray
                    val e = gson.fromJson(errors, Array<Errors>::class.java)
                    e[0].message
                }
            }
            else -> ""
        }
    } catch (ex: Exception) {
        ex.message
    }
