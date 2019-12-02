package app.tourandot.upshot

import java.io.IOException

interface UpshotMapper<T> {
    fun mapSuccess(convertedResponse: Any?): T
    fun mapApiError(errorCode: Int): T
    fun mapNetworkError(exception: IOException): T
}