package com.hus.geofence.authentication

import com.hus.geofence.base.FundationRepo
import com.hus.geofence.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import com.hus.geofence.locationreminders.data.dto.Result

class AuthenticationRepository (
    private val userPreferences: AuthenticationPreference,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FundationRepo(dispatcher) {


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        TODO("Not needed")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("Not needed")
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("Not needed")
    }

    override suspend fun deleteAllReminders() {
        TODO("Not needed")
    }

    private suspend fun saveCredentials(username: String, email: String){
        userPreferences.savingUsrAndPass(username, email)
    }

    suspend fun login(
        email: String, username: String
    ) {
        saveCredentials(username, email)
    }

}
