package studio.bz_soft.freightforwarder.data.models.errors

class HttpError(error: HttpErrorBody): RuntimeException(error.message)

data class HttpErrorBody(
    val code: Int,
    val message: String,
    val response: String
)