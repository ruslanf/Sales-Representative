package studio.bz_soft.freightforwarder.data.http

import retrofit2.http.*
import studio.bz_soft.freightforwarder.data.models.*
import studio.bz_soft.freightforwarder.root.Constants.BASE_API

interface DataApiInterface {

    @POST("$BASE_API/users/sign_up")
    suspend fun signUp(@Body userRequest: UserRequest): AuthResponseModel

    @POST("$BASE_API/users/sign_in")
    suspend fun signIn(@Body userRequest: UserRequest): AuthResponseModel

//    @POST("$BASE_API/users/restore_password")
//    fun restorePassword(@Body email: Email): Call<Unit?>
//
//    @POST("$BASE_API/users/change_password")
//    fun changePassword(@Header("Authorization") token: String,
//                       @Body passwords: Passwords): Call<Unit?>
//
//    @GET("$BASE_API/profile/me")
//    suspend fun getUserProfile(@Header("Authorization") token: String): UserProfileModel
//
//    @PUT("$BASE_API/profile/me")
//    fun updateProfile(@Header("Authorization") token: String,
//                      @Body userProfile: UserProfileModel): Call<Unit?>
//
//    @GET("$BASE_API/trainings")
//    suspend fun getListSimulators(@Header("Authorization") token: String): List<SimulatorsModel>
//
//    @GET("$BASE_API/trainings/{id}")
//    suspend fun getLessons(@Header("Authorization") token: String,
//                           @Path("id") id: Int): LessonsModel
//
//    @GET("$BASE_API/trainings_lessons/{id}")
//    suspend fun getLesson(@Header("Authorization") token: String,
//                          @Path("id") id: Int): TrainingModel
//
//    @PUT("$BASE_API/trainings_lessons/{id}")
//    fun setLessonProgress(@Header("Authorization") token: String,
//                          @Path("id") id: Int,
//                          @Body progressModel: ProgressModel): Call<Unit?>
//
//    @POST("$BASE_API/trainings_lessons/finish")
//    suspend fun finishLesson(@Header("Authorization") token: String,
//                     @Body progress: FProgressModel): FProgressResponseModel
//
//    @GET("$BASE_API/grammars")
//    suspend fun getGrammar(@Header("Authorization") token: String): List<GrammarModel>
//
//    @GET("$BASE_API/personal_words")
//    suspend fun getPersonalWords(@Header("Authorization") token: String): PersonalWordsModel
//
//    @POST("$BASE_API/personal_words")
//    suspend fun addPersonalWord(@Header("Authorization") token: String,
//                                @Body word: Word): Data
//
//    @GET("/api/v1/products/android")
//    suspend fun getAllProducts(@Header("Authorization") token: String): List<ProductModel>
//
//    @POST("/api/v1/payments")
//    suspend fun createPayments(@Header("Authorization") token: String,
//                               @Body paymentsRequest: PaymentsRequest): PaymentsModel
}