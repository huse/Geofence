package com.hus.geofence.authentication

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hus.geofence.MyApp
import com.hus.geofence.base.FundationRepo
import com.hus.geofence.locationreminders.data.ReminderDataSource
import com.hus.geofence.locationreminders.data.local.RemindersLocalRepository
import com.hus.geofence.locationreminders.reminderslist.RemindersListViewModel
import com.hus.geofence.locationreminders.savereminder.SaveReminderViewModel

@Suppress("UNCHECKED_CAST")
class AuthenticationViewModelFactory (
    private val fundationRepo: FundationRepo
) : ViewModelProvider.NewInstanceFactory() {

    val app: MyApp = MyApp()
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthenticationViewModel::class.java) -> AuthenticationViewModel(fundationRepo as AuthenticationRepository,app) as T
            modelClass.isAssignableFrom(RemindersListViewModel::class.java) -> RemindersListViewModel(fundationRepo as ReminderDataSource, app) as T
            modelClass.isAssignableFrom(SaveReminderViewModel::class.java) -> SaveReminderViewModel(
                Application(), fundationRepo as RemindersLocalRepository
            ) as T
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}