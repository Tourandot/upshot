package app.tourandot.upshot

import java.io.IOException

sealed class ResultWithNoValue {
    object Success : ResultWithNoValue()
    object Error : ResultWithNoValue()
}

class ResultWithNoValueMapper :
    UpshotMapper<ResultWithNoValue> {
    override fun mapSuccess(convertedResponse: Any?) =
        ResultWithNoValue.Success
    override fun mapApiError(errorCode: Int) =
        ResultWithNoValue.Error
    override fun mapNetworkError(exception: IOException) =
        ResultWithNoValue.Error
}

sealed class ResultWithValue<out T> {
    data class Success<T>(val value: T) : ResultWithValue<T>()
    object NetworkFailure : ResultWithValue<Nothing>()
    data class ApiError(val errorCode: Int) : ResultWithValue<Nothing>()
}

class ResultWithValueMapper :
    UpshotMapper<ResultWithValue<*>> {
    override fun mapSuccess(convertedResponse: Any?) =
        ResultWithValue.Success(convertedResponse!!)
    override fun mapApiError(errorCode: Int) =
        ResultWithValue.ApiError(errorCode)
    override fun mapNetworkError(exception: IOException) =
        ResultWithValue.NetworkFailure
}