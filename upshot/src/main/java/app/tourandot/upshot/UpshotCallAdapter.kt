package app.tourandot.upshot

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class UpshotCallAdapter<T>(
    private val networkResultType: Type = Unit::class.java,
    private val callAdapterForUnderlyingType: CallAdapter<Any, Call<Any>>,
    private val mapper: UpshotMapper<T>
) : CallAdapter<Any, Call<T>> {
    override fun adapt(call: Call<Any>): Call<T> {
        val adaptedCall = callAdapterForUnderlyingType.adapt(call)
        return DelegatedUpshotCall(
            originalCall = adaptedCall,
            mapper = mapper
        )
    }

    override fun responseType() = networkResultType
}