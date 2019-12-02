package app.tourandot.upshot

import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

internal class DelegatedUpshotCall<T>(
    private val originalCall: Call<Any>,
    private val mapper: UpshotMapper<T>
) : Call<T> {
    override fun enqueue(callback: Callback<T>) {
        originalCall.enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, error: Throwable) {
                if (error is IOException) {
                    callback.onResponse(
                        this@DelegatedUpshotCall,
                        Response.success(mapper.mapNetworkError(error))
                    )
                } else {
                    callback.onFailure(this@DelegatedUpshotCall, error)
                }
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (originalCall.isCanceled) {
                    return
                }

                val mappedResponse = if (response.isSuccessful) {
                    mapper.mapSuccess(response.body())
                } else {
                    mapper.mapApiError(response.code())
                }
                callback.onResponse(
                    this@DelegatedUpshotCall,
                    Response.success(mappedResponse)
                )
            }
        })
    }

    override fun isExecuted() = originalCall.isExecuted

    override fun clone(): Call<T> =
        DelegatedUpshotCall(originalCall, mapper)

    override fun isCanceled() = originalCall.isCanceled

    override fun cancel() = originalCall.cancel()

    override fun execute(): Response<T> {
        throw IllegalStateException("Only enqueue is supported")
    }

    override fun request(): Request = originalCall.request()
}