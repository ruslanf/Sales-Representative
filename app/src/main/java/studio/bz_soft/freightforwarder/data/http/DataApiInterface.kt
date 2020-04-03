package studio.bz_soft.freightforwarder.data.http

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import studio.bz_soft.freightforwarder.data.models.*
import studio.bz_soft.freightforwarder.root.Constants.BASE_API

interface DataApiInterface {

    @POST("$BASE_API/users/sign_up")
    suspend fun signUp(@Body userRequest: UserRequest): AuthResponseModel

    @POST("$BASE_API/users/sign_in")
    suspend fun signIn(@Body userRequest: UserRequest): AuthResponseModel

    @POST("$BASE_API/users/restore_password")
    fun restorePassword(@Body email: Email): Call<Unit?>

    @POST("$BASE_API/users/change_password")
    fun changePassword(@Header("Authorization") token: String,
                       @Body passwords: Passwords): Call<Unit?>

    @GET("$BASE_API/users/managers")
    suspend fun getManagersList(@Header("Authorization") token: String): List<ManagersModel>

    @GET("$BASE_API/users/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): UserProfileModel

    @PUT("$BASE_API/users/profile")
    fun updateProfile(@Header("Authorization") token: String,
                      @Body userProfile: UserProfileModel): Call<Unit?>

    @Multipart
    @POST("$BASE_API/image/upload")
    suspend fun uploadImage(@Header("Authorization") token: String,
                            @Part image: MultipartBody.Part): List<ImageModel>
}