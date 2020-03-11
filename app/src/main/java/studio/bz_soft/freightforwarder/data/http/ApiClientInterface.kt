package studio.bz_soft.freightforwarder.data.http

import studio.bz_soft.freightforwarder.data.models.*

interface ApiClientInterface {
    suspend fun signUp(userRequest: UserRequest): AuthResponseModel
    suspend fun signIn(userRequest: UserRequest): AuthResponseModel

//    suspend fun restorePassword(email: Email): Unit?
//    suspend fun changePassword(token: String, passwords: Passwords): Unit?
//
//    suspend fun loadUserProfile(token: String): UserProfileModel
//    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Unit?
//
//    suspend fun getListSimulators(token: String): List<SimulatorsModel>
//    suspend fun getLessons(token: String, id: Int): LessonsModel
//    suspend fun getLesson(token: String, id: Int): TrainingModel
//    suspend fun setLessonProgress(token: String, id: Int, progressModel: ProgressModel): Unit?
//    suspend fun finishLesson(token: String, finish: FProgressModel): FProgressResponseModel
//
//    suspend fun getGrammar(token: String): List<GrammarModel>
//
//    suspend fun getPersonalWords(token: String): PersonalWordsModel
//    suspend fun addPersonalWord(token: String, word: Word): Data
//
//    suspend fun getAllProducts(token: String): List<ProductModel>
//    suspend fun createPayments(token: String, paymentsRequest: PaymentsRequest): PaymentsModel
}