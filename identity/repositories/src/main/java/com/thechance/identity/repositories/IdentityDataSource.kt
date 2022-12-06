package com.thechance.identity.repositories

import com.thechance.identity.repositories.models.AccountDTO
import com.thechance.identity.repositories.models.UserDataDTO

interface IdentityDataSource {

    suspend fun login(userName: String, password: String): Boolean //UserDTO

    suspend fun signup(userDataDTO: UserDataDTO): AccountDTO
}