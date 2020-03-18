package studio.bz_soft.freightforwarder.data.http

import studio.bz_soft.freightforwarder.data.models.*

interface ApiClientInterface {
    suspend fun signUp(userRequest: UserRequest): AuthResponseModel
    suspend fun signIn(userRequest: UserRequest): AuthResponseModel

    suspend fun restorePassword(email: Email): Unit?
    suspend fun changePassword(token: String, passwords: Passwords): Unit?

    suspend fun loadUserProfile(token: String): UserProfileModel
    suspend fun updateProfile(token: String, userProfile: UserProfileModel): Unit?
}