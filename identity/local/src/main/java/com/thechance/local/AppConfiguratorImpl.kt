package com.thechance.local

import com.thechance.identity.repositories.LocalIdentityDataSource
import javax.inject.Inject

class AppConfiguratorImpl @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : LocalIdentityDataSource {

//    override fun getSignupState() {
//        dataStorePreferences.readString(SIGN_UP_STATE_KEY)
//    }
//
//    override suspend fun setSignupState(value: String) {
//        dataStorePreferences.writeString(SIGN_UP_STATE_KEY, value)
//    }
//
//    override fun getStartInstallState() {
//        dataStorePreferences.readBoolean(START_INSTALL_STATE_KEY)
//    }
//
//    override suspend fun setStartInstallState(value: Boolean) {
//        dataStorePreferences.writeBoolean(START_INSTALL_STATE_KEY, value)
//    }


    companion object DataStorePreferencesKeys {
        const val SIGN_UP_STATE_KEY = "sign_up_state_key"
        const val START_INSTALL_STATE_KEY = "start_install_state_key"
    }

    override fun getStartInstall(): Boolean? {
        return dataStorePreferences.readBoolean(START_INSTALL_STATE_KEY)
    }

    override suspend fun setStartInstall(value: Boolean) {
        return dataStorePreferences.writeBoolean(START_INSTALL_STATE_KEY, value)
    }

    override fun getUserId(): String? {
        return dataStorePreferences.readString(SIGN_UP_STATE_KEY)
    }

    override suspend fun saveUserId(id: String) {
        return dataStorePreferences.writeString(SIGN_UP_STATE_KEY, id)
    }
}