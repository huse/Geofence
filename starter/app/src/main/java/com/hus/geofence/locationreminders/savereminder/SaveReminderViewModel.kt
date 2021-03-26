package com.hus.geofence.locationreminders.savereminder

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.hus.geofence.R
import com.hus.geofence.base.BaseViewModel
import com.hus.geofence.base.NavigationCommand
import com.hus.geofence.locationreminders.data.ReminderDataSource
import com.hus.geofence.locationreminders.data.dto.ReminderDTO
import com.hus.geofence.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(override val app: Application, private val reminderDataSource: ReminderDataSource) :
    BaseViewModel(app) {
    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    val reminderSelectedLocationStr = MutableLiveData<String>()
    val selectedPOI = MutableLiveData<PointOfInterest>()
    val latitude = MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()
    val idForreminder = MutableLiveData<String>()
    val TAG = "SaveReminderViewModel"

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        selectedPOI.value = null
        latitude.value = null
        longitude.value = null
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(reminderDataItem: ReminderDataItem) : Boolean {
        if (validateEnteredData(reminderDataItem)) {
            saveReminder(reminderDataItem)
            return true
        }
        return false
    }

    /**
     * Save the reminder to the data source
     */
    fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            reminderDataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }

    fun settingValuesToReminder(data: ReminderDataItem) {
        reminderTitle.value = data.title
        reminderDescription.value = data.description
        reminderSelectedLocationStr.value = data.location
        latitude.value = data.latitude
        longitude.value = data.longitude
        idForreminder.value = data.id
    }
    fun deletingData() {
        showLoading.value = false
    }

    fun positionUpdate(
        latLng: LatLng,
        snippet: String,
        pointOfInterest: PointOfInterest? = null
    ) {
        Log.d(TAG, "updating latLng and snipped:" +snippet)
        selectedPOI.postValue(pointOfInterest)
        latitude.postValue(latLng.latitude)
        longitude.postValue(latLng.longitude)
        reminderSelectedLocationStr.postValue(snippet)
    }
}