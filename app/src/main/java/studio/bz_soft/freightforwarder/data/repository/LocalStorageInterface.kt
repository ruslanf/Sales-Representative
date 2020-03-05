package studio.bz_soft.freightforwarder.data.repository

interface LocalStorageInterface {
    fun setUserToken(userToken: String)
    fun getUserToken(): String?
    fun deleteToken()

    fun setUserId(id: Int)
    fun deleteUserId()

    fun setLegacyId(legacyId: Int)
}