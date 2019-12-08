package app.tourandot.upshot

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.http.GET

private interface CoroutineService {
    @GET("/")
    suspend fun withBody(): ResultWithValue<String>

    @GET("/")
    suspend fun noBody(): ResultWithNoValue
}

class SuspendTest {
    @get:Rule
    val mockServer = MockWebServer()

    private val service by lazy(LazyThreadSafetyMode.NONE) {
        val retrofit = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(StringConverterFactory())
            .addCallAdapterFactory(
                UpshotCallAdapterFactory.createWithMapper(ResultWithValueMapper())
            )
            .addCallAdapterFactory(
                UpshotCallAdapterFactory.createWithMapper(ResultWithNoValueMapper())
            )
            .build()

        retrofit.create(CoroutineService::class.java)
    }

    @Test
    fun withBody_success() = runBlocking {
        mockServer.enqueue(MockResponse().setBody("Hi"))

        val actualResult = service.withBody()
        assertThat(actualResult).isEqualTo(ResultWithValue.Success("Hi"))
    }

    @Test
    fun withBody_serverError() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(400))

        val actualResult = service.withBody()
        assertThat(actualResult).isEqualTo(ResultWithValue.ApiError(400))
    }

    @Test
    fun withBody_networkError() = runBlocking {
        mockServer.enqueue(MockResponse().apply {
            socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        })

        val actualResult = service.withBody()
        assertThat(actualResult).isEqualTo(ResultWithValue.NetworkFailure)
    }

    @Test
    fun noBody_success() = runBlocking {
        mockServer.enqueue(MockResponse())

        val actualResult = service.noBody()
        assertThat(actualResult).isEqualTo(ResultWithNoValue.Success)
    }

    @Test
    fun noBody_serverError() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(400))

        val actualResult = service.noBody()
        assertThat(actualResult).isEqualTo(ResultWithNoValue.Error)

    }

    @Test
    fun noBody_networkError() = runBlocking {
        mockServer.enqueue(MockResponse().apply {
            socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        })

        val actualResult = service.noBody()
        assertThat(actualResult).isEqualTo(ResultWithNoValue.Error)
    }
}
