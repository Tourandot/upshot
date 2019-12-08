package app.tourandot.upshot

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

class UpshotCallAdapterFactory<T : Any>(
    private val forType: KClass<T>,
    private val mapper: UpshotMapper<T>
) : CallAdapter.Factory() {
    companion object {
        inline fun <reified T : Any> createWithMapper(mapper: UpshotMapper<T>): UpshotCallAdapterFactory<T> {
            return UpshotCallAdapterFactory(T::class, mapper)
        }
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val upshotType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(upshotType) != forType.java) {
            return null
        }

        val actualResponseType: Type = if (upshotType is ParameterizedType) {
            getParameterUpperBound(0, upshotType)
        } else {
            Unit::class.java
        }

        @Suppress("UNCHECKED_CAST")
        val defaultAdapterForActualResponse = retrofit.nextCallAdapter(
            null,
            CallParameterizedType(actualResponseType),
            annotations
        ) as CallAdapter<Any, Call<Any>>

        return UpshotCallAdapter(
            networkResultType = actualResponseType,
            callAdapterForUnderlyingType = defaultAdapterForActualResponse,
            mapper = mapper
        )
    }
}

private class CallParameterizedType(private val responseType: Type) : ParameterizedType {
    override fun getRawType(): Type? {
        return Call::class.java
    }

    override fun getOwnerType(): Type? {
        return null
    }

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(responseType)
    }
}