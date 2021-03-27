package com.hus.geofence.locationreminders.data

import com.hus.geofence.base.FundationRepo
import com.hus.geofence.locationreminders.data.dto.ReminderDTO
import com.hus.geofence.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (
    private var reminders: MutableList<ReminderDTO>? = mutableListOf()
) : FundationRepo(Dispatchers.Main) {

    fun setReturnError(shouldReturnError: Boolean) {
        when (shouldReturnError){
            true -> { this.reminders = null }
        }
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return reminders.getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO) : Result<Any> = callBackData {
        when (val result = reminders.addReminder(reminder)) {
            true -> { Result.Success(result) }
            false -> { Result.Error("Error reminder!") }
            else -> {Result.Error("Unknown error") }
        }
    }

    private fun MutableList<ReminderDTO>?.addReminder(reminderDTO: ReminderDTO) : Boolean {
        this?.let {
            add(reminderDTO)
            return true
        }
        return false
    }
    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val result = callBackData {
            reminders.getReminderByID(id)
        }
        return when(result){
            is Result.Success -> result.data
            is Result.Error -> Result.Error(result.message.toString())
            Result.Loading -> Result.Error("Error")
        }
    }

    override suspend fun deleteAllReminders() {
        callBackData {
            reminders.clearAll()
        }
    }
    private fun MutableList<ReminderDTO>?.clearAll() : Boolean {
        this?.let {
            clear()
            return true
        }
        return false
    }
    private fun MutableList<ReminderDTO>?.getReminders() : Result<List<ReminderDTO>> {
        this?.let {
            return Result.Success(it)
        }
        return Result.Error("Not exist")
    }
    private fun MutableList<ReminderDTO>?.getReminderByID(id: String) : Result<ReminderDTO> {
        this?.firstOrNull {
            it.id == id
        }?.let {
            return Result.Success(it)
        }
        return Result.Error("ID!???")
    }

}