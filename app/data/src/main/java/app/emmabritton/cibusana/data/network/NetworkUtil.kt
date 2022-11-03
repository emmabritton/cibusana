package app.emmabritton.cibusana.data.network

import app.emmabritton.cibusana.data.Logger
import app.emmabritton.cibusana.data.models.ResponseWrapper
import retrofit2.Call
import java.io.IOException

fun <R> executeRequest(logger: Logger, call: Call<ResponseWrapper<R>>): Result<R> {
    return try {
        val response = call.execute()
        if (response.isSuccessful) {
            val body = response.body()
            if (body == null || (body.content == null && body.error == null)) {
                logger.e("No response body for $call")
                Result.failure(EmptyResponseException())
            } else {
                if (body.content != null) {
                    Result.success(body.content)
                } else {
                    logger.e("Bad request (${body.error!!.codes}) for $call")
                    Result.failure(BadRequestException(body.error.codes, body.error.message))
                }
            }
        } else {
            logger.e("Server Error (${response.code()}) for $call")
            Result.failure(ServerErrorException(response.code()))
        }
    } catch (e: IOException) {
        logger.e(e, "Network error for $call")
        Result.failure(e)
    } catch (e: Exception) {
        logger.e(e, "Unknown error for $call")
        Result.failure(e)
    }
}