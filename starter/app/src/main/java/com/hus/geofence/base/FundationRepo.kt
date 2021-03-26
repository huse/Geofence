package com.hus.geofence.base

import com.hus.geofence.locationreminders.data.ReminderDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.hus.geofence.locationreminders.data.dto.Result

abstract class FundationRepo(
    private val coroutineDispatcher: CoroutineDispatcher
) : ReminderDataSource {

    suspend fun <T : Any> callBackData(
        apiCall: suspend () -> T
    ) : Result<T> {
        return withContext(coroutineDispatcher){
            try {
                Result.Success(apiCall.invoke())
            }catch(throwable: Throwable){
                Result.Error(throwable.message, null)
            }
        }
    }

}