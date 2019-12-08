# Upshot
Retrofit Call Adapter that converts return types of retrofit API definitions into any custom result class.

## Installation
`implementation("app.tourandot:upshot:0.0.3")`

## Usage
  
  1. Define a custom result type: 
       ```
       sealed class NetworkResult<out T> {
          data class Success<T>(val value: T) : NetworkResult<T>()
          object NetworkFailure : NetworkResult<Nothing>()
          data class ApiError(val errorCode: Int) : NetworkResult<Nothing>()
      }
      ```
  1. Map to the desired type:
        ```
         object NetworkResultMapper : UpshotMapper<NetworkResult> {
             override fun mapSuccess(convertedResponse: Any?) = NetworkResult.Success(convertedResponse!!)
             override fun mapApiError(errorCode: Int) = NetworkResult.ApiError(errorCode)
             override fun mapNetworkError(exception: IOException) = NetworkResult.NetworkFailure
         }
        ```
  
  1. Register with retrofit: 
      ```
      retrofitBuilder.addCallAdapterFactory(
        UpshotCallAdapterFactory.createWithMapper(NetworkResultMapper)
      )
      ```
  1. Use the result type in a retrofit service:
  
        ```
        interface SwampService {
              @GET("/friends")
              suspend fun friends(): NetworkResult<List<Friend>>
        }
        ```      
            
## Limitations
Currently only supports `suspend fun` API definitions