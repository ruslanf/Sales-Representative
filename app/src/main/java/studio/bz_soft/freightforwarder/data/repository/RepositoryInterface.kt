package studio.bz_soft.freightforwarder.data.repository

import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.models.*

interface RepositoryInterface {
    fun setUserToken(userToken: String)
    fun getUserToken(): String?
    fun deleteToken()

    fun setUserId(id: Int)
    fun deleteUserId()

    fun setLegacyId(legacyId: Int)

//    suspend fun sendRegistrationData(userRequest: UserRequest): Either<Exception, AuthResponseModel>
//    suspend fun sendAuthData(userRequest: UserRequest): Either<Exception, AuthResponseModel>
//    suspend fun sendFbAuthData(fbLoginModel: FbLoginModel): Either<Exception, AuthResponseModel>
//    suspend fun sendVkAuthData(vkLoginModel: VkLoginModel): Either<Exception, AuthResponseModel>
//
//    suspend fun restorePassword(email: Email): Either<Exception, Unit?>
//    suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?>
//
//    suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel>
//    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?>
//
//    suspend fun getListSimulators(token: String): Either<Exception, List<SimulatorsModel>>
//    suspend fun getLessons(token: String, id: Int): Either<Exception, LessonsModel>
//    suspend fun getLesson(token: String, id: Int): Either<Exception, TrainingModel>
//    suspend fun setLessonProgress(token: String, id: Int, progressModel: ProgressModel): Either<Exception, Unit?>
//    suspend fun finishLesson(token: String, finish: FProgressModel): Either<Exception, FProgressResponseModel>
//
//    suspend fun getGrammar(token: String): Either<Exception, List<GrammarModel>>
//
//    suspend fun getPersonalWords(token: String): Either<Exception, PersonalWordsModel>
//    suspend fun addPersonalWord(token: String, word: Word): Either<Exception, Data>
//
//    suspend fun getAllProducts(token: String): Either<Exception, List<ProductModel>>
//    suspend fun createPayments(token: String, paymentsRequest: PaymentsRequest): Either<Exception, PaymentsModel>
}