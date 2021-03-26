package com.hus.geofence.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.hus.geofence.R
import com.hus.geofence.base.NavigationCommand
import com.hus.geofence.base.SuperBaseSaveReminder
import com.hus.geofence.databinding.FragmentSaveReminderBinding
import com.hus.geofence.locationreminders.geofence.GeofenceBroadcastReceiver
import com.hus.geofence.locationreminders.reminderslist.ReminderDataItem
import com.hus.geofence.utils.GeoFenceConstant
import com.hus.geofence.utils.hideKeyboard
import com.hus.geofence.utils.setDisplayHomeAsUpEnabled
import java.util.*

class SaveReminderFragment : SuperBaseSaveReminder<FragmentSaveReminderBinding>() {
    //Get the view model this time as a single to be shared with the another fragment

    companion object {
        private const val TAG = "SaveReminderFragment"
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = GeoFenceConstant.GEO_FENCE_EVENT
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private lateinit var reminderDataItem1: ReminderDataItem
    private val args: SaveReminderFragmentArgs by navArgs()
    private lateinit var geofencingClient: GeofencingClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myContext = requireContext()
        args.reminder?.let { reminderDataItem ->
            reminderDataItem1 = reminderDataItem
            saveReminderViewModel.apply {
                settingValuesToReminder(reminderDataItem1)
            }
        }
        saveReminderViewModel.deletingData()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setDisplayHomeAsUpEnabled(true)
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = saveReminderViewModel
        geofencingClient = LocationServices.getGeofencingClient(myContext)
        return viewBinding.root
    }
    private fun initialReminderData() {
        reminderDataItem1 = ReminderDataItem(
                saveReminderViewModel.reminderTitle.value,
                saveReminderViewModel.reminderDescription.value,
                saveReminderViewModel.reminderSelectedLocationStr.value,
                saveReminderViewModel.latitude.value,
                saveReminderViewModel.longitude.value,
                saveReminderViewModel.idForreminder.value ?: UUID.randomUUID().toString()
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.lifecycleOwner = this
        viewBinding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            initialReminderData()
            hideKeyboard()
            saveReminderViewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        viewBinding.saveReminder.setOnClickListener {
            initialReminderData()
            if (saveReminderViewModel.validateAndSaveReminder(reminderDataItem1)) {
                addingReminderToGeofence(
                    reminderDataItem1.latitude!!,
                    reminderDataItem1.longitude!!,
                    reminderDataItem1.id
                )
                hideKeyboard()
                saveReminderViewModel.onClear()
            }
//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        saveReminderViewModel.onClear()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaveReminderBinding.inflate(inflater, container, false)

    @SuppressLint("MissingPermission")
    private fun addingReminderToGeofence(latitude: Double, longitude: Double, reminderId: String) {
        val geoFence = Geofence.Builder()
            .setRequestId(reminderId)
            .setCircularRegion(
                latitude,
                longitude,

                GeoFenceConstant.GEO_FENCE_RADIUS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geoFencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geoFence)
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geoFencingRequest, geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        Log.d(TAG, "hhhh    Geo fence id :  " + reminderId)
                    }
                    addOnFailureListener {
                        saveReminderViewModel.showErrorMessage.postValue(getString(R.string.error_adding_geofence))
                        it.message?.let { message ->
                            Log.w(TAG, message)
                        }
                    }
                }
            }
        }
    }



}
