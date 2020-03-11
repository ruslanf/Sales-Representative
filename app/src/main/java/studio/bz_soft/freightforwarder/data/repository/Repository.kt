package studio.bz_soft.freightforwarder.data.repository

import org.koin.core.KoinComponent
import studio.bz_soft.freightforwarder.data.http.ApiClientInterface
import studio.bz_soft.freightforwarder.data.http.Either
import studio.bz_soft.freightforwarder.data.http.safeRequest
import studio.bz_soft.freightforwarder.data.models.*

class Repository(
    private val storage: LocalStorageInterface,
    private val client: ApiClientInterface
) : RepositoryInterface, KoinComponent {

    override fun setUserToken(userToken: String) {
        storage.setUserToken(userToken)
    }

    override fun getUserToken(): String? = storage.getUserToken()

    override fun deleteToken() {
        storage.deleteToken()
    }

    override fun setUserId(id: Int) {
        storage.setUserId(id)
    }

    override fun deleteUserId() {
        storage.deleteUserId()
    }

    override suspend fun sendRegistrationData(userRequest: UserRequest): Either<Exception, AuthResponseModel> =
        safeRequest { client.signUp(userRequest) }

    override suspend fun sendAuthData(userRequest: UserRequest): Either<Exception, AuthResponseModel> =
        safeRequest { client.signIn(userRequest) }

//    override suspend fun restorePassword(email: Email): Either<Exception, Unit?> =
//        safeRequest { client.restorePassword(email) }
//
//    override suspend fun changePassword(token: String, passwords: Passwords): Either<Exception, Unit?> =
//        safeRequest { client.changePassword(token, passwords) }
//
//    override suspend fun loadUserProfile(token: String): Either<Exception, UserProfileModel> =
//        safeRequest { client.loadUserProfile(token) }
//
//    override suspend fun updateProfile(token: String, userProfile: UserProfileModel): Either<Exception, Unit?> =
//        safeRequest { client.updateProfile(token, userProfile) }
//
//    override suspend fun getListSimulators(token: String): Either<Exception, List<SimulatorsModel>> =
//        safeRequest { client.getListSimulators(token) }
//
//    override suspend fun getLessons(token: String, id: Int): Either<Exception, LessonsModel> =
//        safeRequest { client.getLessons(token, id) }
//
//    override suspend fun getLesson(token: String, id: Int): Either<Exception, TrainingModel> =
//        safeRequest { client.getLesson(token, id) }
//
//    override suspend fun setLessonProgress(token: String, id: Int, progressModel: ProgressModel): Either<Exception, Unit?> =
//        safeRequest { client.setLessonProgress(token, id, progressModel) }
//
//    override suspend fun finishLesson(token: String, finish: FProgressModel): Either<Exception, FProgressResponseModel> =
//        safeRequest { client.finishLesson(token, finish) }
//
//    override suspend fun getGrammar(token: String): Either<Exception, List<GrammarModel>> =
//        safeRequest { client.getGrammar(token) }
//
//    override suspend fun getPersonalWords(token: String): Either<Exception, PersonalWordsModel> =
//        safeRequest { client.getPersonalWords(token) }
//
//    override suspend fun addPersonalWord(token: String, word: Word): Either<Exception, Data> =
//        safeRequest { client.addPersonalWord(token, word) }
//
//    override suspend fun getAllProducts(token: String): Either<Exception, List<ProductModel>> =
//        safeRequest { client.getAllProducts(token) }
//
//    override suspend fun createPayments(token: String, paymentsRequest: PaymentsRequest): Either<Exception, PaymentsModel> =
//        safeRequest { client.createPayments(token, paymentsRequest) }
}