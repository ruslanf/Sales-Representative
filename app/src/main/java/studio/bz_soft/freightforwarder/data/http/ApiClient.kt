package studio.bz_soft.freightforwarder.data.http

import android.content.Context
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import studio.bz_soft.freightforwarder.BuildConfig
import studio.bz_soft.freightforwarder.data.models.*
import java.io.File
import java.util.*
import javax.net.ssl.HostnameVerifier

class ApiClient(
    private val apiURL: String,
    private val appContext: Context
): ApiClientInterface {

    private val retrofitClient by lazy { createRetrofitClient(apiURL) }
    private val apiClient by lazy { retrofitClient.create(DataApiInterface::class.java) }

    private val cacheSize = (10 * 1024 * 1024).toLong()
    private val httpCacheDirectory = File(appContext.cacheDir, "offlineCache")
    private val httpCache = Cache(httpCacheDirectory, cacheSize)

    private val logger = run {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE }
    }

    private val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_2)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
        )
        .build()

    private fun httpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .cache(httpCache)
            .hostnameVerifier(HostnameVerifier { _, _ -> true }) // Added due javax.net.ssl.SSLPeerUnverifiedException
            .connectionSpecs(Collections.singletonList(connectionSpec))
            .addNetworkInterceptor(cacheInterceptor())
            .addInterceptor(offlineCacheInterceptor(appContext))
            .addInterceptor(logger)
            .build()

    private fun createRetrofitClient(apiURL: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(apiURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient())
            .build()

    override suspend fun signUp(userRequest: UserRequest): AuthResponseModel = apiClient.signUp(userRequest)
    override suspend fun signIn(userRequest: UserRequest): AuthResponseModel = apiClient.signIn(userRequest)

    override suspend fun restorePassword(email: Email): Unit? = apiClient.restorePassword(email).await()
    override suspend fun changePassword(token: String, passwords: Passwords): Unit? =
        apiClient.changePassword(modifiedToken(token), passwords).await()

    override suspend fun getManagersList(token: String): List<ManagersModel> = apiClient.getManagersList(modifiedToken(token))
    override suspend fun loadUserProfile(token: String): UserProfileModel = apiClient.getUserProfile(modifiedToken(token))
    override suspend fun updateProfile(token: String, userProfile: UserProfileModel): Unit? =
        apiClient.updateProfile(modifiedToken(token), userProfile).await()

    override suspend fun uploadImage(token: String, image: MultipartBody.Part): List<ImageModel> =
        apiClient.uploadImage(modifiedToken(token), image)

    override suspend fun saveTradePoint(token: String, storePoint: StorePointModel): Unit? =
        apiClient.saveTradePoint(modifiedToken(token), storePoint).await()

    override suspend fun getTradePointList(token: String): List<StorePointModel> =
        apiClient.getTradePointList(modifiedToken(token))

    override suspend fun getTradePoint(token: String, id: Int): StorePointModel =
        apiClient.getTradePoint(modifiedToken(token), id)

    override suspend fun updateTradePoint(token: String, id: Int, storePoint: StorePointModel): Unit? =
        apiClient.updateTradePoint(modifiedToken(token), id, storePoint).await()

    override suspend fun syncTradePoint(token: String, listStorePoint: List<StorePointModel>): Unit? =
        apiClient.syncTradePoint(token, listStorePoint).await()

    private fun modifiedToken(token: String): String = "Bearer $token"
}