package com.hus.geofence.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.hus.geofence.R
import com.hus.geofence.base.NavigationCommand
import com.hus.geofence.base.SuperBaseSaveReminder
import com.hus.geofence.databinding.FragmentSelectLocationBinding
import com.hus.geofence.locationreminders.data.local.LocalDB
import com.hus.geofence.locationreminders.data.local.RemindersDao
import com.hus.geofence.locationreminders.reminderslist.ReminderDataItem
import com.hus.geofence.locationreminders.reminderslist.RemindersListViewModel
import org.koin.android.ext.android.inject

class SelectLocationFragment : SuperBaseSaveReminder<FragmentSelectLocationBinding>() , OnMapReadyCallback {

    private val selectLocationFragmentArgs: SelectLocationFragmentArgs by navArgs()
    private val locationSydney : LatLng = LatLng(-34.0, 151.0)
    private val normalView = 20f
    private var marker: Marker? = null
    //private lateinit var map: GoogleMap
    private lateinit var remindersDao: RemindersDao

    private var doesHavePermission = false
    private var previuosLocation : Location? = null
    private var locationOfCamera : CameraPosition? = null

    //Use Koin to get the view model of the SaveReminder

    private val remindersViewModel: RemindersListViewModel by inject()
    private lateinit var mMap: GoogleMap


    override fun onMapReady(googleMap: GoogleMap) {
        // resource : https://developers.google.com/maps/documentation/android-sdk/start#None-kotlin
        mMap = googleMap
        Log.d("SelectLocationFragment", "hhhh  onMapReady  called.  map:  $mMap")

        if (selectLocationFragmentArgs.reminder != null){
            initSaveLocationClickListeners()
            addMarkerer(selectLocationFragmentArgs.reminder!!, true)
        } else {
            remindersViewModel.loadReminders()
            remindersViewModel.remindersList.observe(viewLifecycleOwner, {
                for (i in it){
                    addMarkerer(i, false)
                }
            })
            initSaveLocationClickListeners()

        }

        updateLocationUI()
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val TAG = "SelectLocationFrag"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d("SelectLocationFragment", "hhhh  onCreateView  called.  " )

     /*   binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)
*/
        viewBinding.viewModel = saveReminderViewModel
        viewBinding.lifecycleOwner = this

        setHasOptionsMenu(true)
       // setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id._map_fragment_id) as SupportMapFragment
        mapFragment.getMapAsync(this)
//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location
       // onLocationSelected()

        return viewBinding.root
    }
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        Log.d("SelectLocationFragment", "hhhh  getLocationPermission  called.  " )

        if (ContextCompat.checkSelfPermission(myContext.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Log.d("SelectLocationFragment", "hhhh  getLocationPermission  called.  true " )

            doesHavePermission = true
            updateLocationUI()
            onLocationSelected()
        } else {

            Log.d("SelectLocationFragment", "hhhh  getLocationPermission  called.  false " )
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSelectLocationBinding.inflate(inflater, container, false)

    override fun onRequestPermissionsResult(

        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        Log.d("SelectLocationFragment", "hhhh  onRequestPermissionsResult  called.  " )

        doesHavePermission = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doesHavePermission = true
                }
            }
        }
        updateLocationUI()
    }
    private fun updateLocationUI() {
        Log.d("SelectLocationFragment", "hhhh  updateLocationUI  called." )

        try {
            if (doesHavePermission) {
                Log.d("SelectLocationFragment", "hhhh  updateLocationUI 1 called.  true " )

                mMap.isMyLocationEnabled = true
                Log.d("SelectLocationFragment", "hhhh  updateLocationUI 2 called.  true " )

                mMap.uiSettings?.isMyLocationButtonEnabled = true
                Log.d("SelectLocationFragment", "hhhh  updateLocationUI 3 called.  true " )

            } else {
                Log.d("SelectLocationFragment", "hhhh  updateLocationUI  called.  false " )

                mMap.isMyLocationEnabled = false
                mMap.uiSettings?.isMyLocationButtonEnabled = false
                previuosLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence

        Log.d("SelectLocationFragment", "hhhh  onLocationSelected  called." )


        try {
            if (doesHavePermission) {
                val locationResult = FusedLocationProviderClient(myContext).lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {


                        Log.d("SelectLocationFragment", "hhhh  onLocationSelected  called. 1 true " )
                        // Set the map's camera position to the current location of the device.
                        previuosLocation = task.result
                        if (previuosLocation != null && selectLocationFragmentArgs.reminder == null) {
                            Log.d("SelectLocationFragment", "hhhh  onLocationSelected  called. 2 true " )

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(previuosLocation?.latitude ?: locationSydney.latitude,
                                    previuosLocation?.longitude ?: locationSydney.longitude), normalView
                            ))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(locationSydney, normalView))
                        mMap.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(locationSydney, normalView))
            mMap.uiSettings?.isMyLocationButtonEnabled = false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("SelectLocationFragment", "hhhh  onCreateOptionsMenu  called.  " )

        inflater.inflate(R.menu.map_options, menu)
    }


    private fun initSaveLocationClickListeners() {
        Log.d("SelectLocationFragment", "hhhh  initSaveLocationClickListeners  called.  " )
        viewBinding.btnSave.visibility = View.VISIBLE
        viewBinding.btnSave.setOnClickListener {
            Log.d("SelectLocationFragment", "hhhh  save button   clicked.  " )
            if (marker == null) {
                marker?.remove()
/*                val sydney = LatLng(-34.0, 151.0)
                //val snippet = "${it.latitude}, ${it.longitude}"
                Log.d(TAG, "initSaveLocationClickListeners: $sydney")
                saveReminderViewModel.updateSelectedLocation( sydney,"sydney")*/
                saveReminderViewModel.showErrorMessage.value = ("Select a location on Map")

               // saveReminderViewModel.navigationCommand.postValue(NavigationCommand.Back)
            } else {
                saveReminderViewModel.navigationCommand.postValue(NavigationCommand.Back)
            }
        }
//resource :  https://developers.google.com/maps/documentation/android-sdk/poi

        mMap.setOnPoiClickListener {
            Log.d("SelectLocationFragment", "hhhh  setOnPoiClickListener  clicked.  " )

            marker?.remove()

            val snippet = it.name
            Log.d(TAG, "initSaveLocationClickListeners: $snippet")
            saveReminderViewModel.positionUpdate(it.latLng, snippet, it)

            marker = mMap.addMarker(
                MarkerOptions().position(it.latLng)
                    .title("Selected point")
                    .snippet(snippet)
            )

        }



        mMap.setOnMapClickListener {
            Log.d("SelectLocationFragment", "hhhh  setOnPoiClickListener  clicked.  " )
            marker?.remove()
            val snippet = "${it.latitude}, ${it.longitude}"
            Log.d(TAG, "initSaveLocationClickListeners: $snippet")
            saveReminderViewModel.positionUpdate(it, snippet)

            marker = mMap.addMarker(
                MarkerOptions().position(it)
                    .title("Selected point")
                    .snippet(snippet)
            )
        }
    }

    private fun addMarkerer(data: ReminderDataItem, single: Boolean) {
        if (data.latitude != null && data.longitude != null){
            val latLng = LatLng(data.latitude!!, data.longitude!!)
            if (single){
                marker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(data.title)
                        .snippet(data.description)
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    latLng, normalView
                ))
            } else {
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(data.title)
                        .snippet(data.description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(::mMap.isInitialized){
            when (item.itemId) {
                R.id.normal_map -> { mMap.mapType = GoogleMap.MAP_TYPE_NORMAL; return true }
                R.id.hybrid_map -> { mMap.mapType = GoogleMap.MAP_TYPE_HYBRID; return true }
                R.id.satellite_map -> { mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE; return true }
                R.id.terrain_map -> { mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN; return true }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myContext = requireContext()
        remindersDao = LocalDB.createRemindersDao(myContext)
        if (savedInstanceState != null) {
            previuosLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            locationOfCamera = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        mMap.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, previuosLocation)
        }
        super.onSaveInstanceState(outState)
    }

}
