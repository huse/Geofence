package com.hus.geofence.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.hus.geofence.authentication.AuthenticationActivity
import com.hus.geofence.authentication.AuthenticationPreference
import com.hus.geofence.locationreminders.data.local.LocalDB
import com.hus.geofence.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

abstract class SuperBaseSaveReminder<viewBinding: ViewBinding> : Fragment(){

    protected lateinit var authenticationPreference: AuthenticationPreference
    protected lateinit var viewBinding : viewBinding
    protected lateinit var myContext: Context

    protected val saveReminderViewModel: SaveReminderViewModel by inject()

    //protected val remoteDataSource = RemoteDataSource()



    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : viewBinding

    override fun onStart() {
        super.onStart()
        saveReminderViewModel.showErrorMessage.observe(this, {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        saveReminderViewModel.showToast.observe(this, {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        saveReminderViewModel.showSnackBar.observe(this, {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })
        saveReminderViewModel.showSnackBarInt.observe(this, {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })

        saveReminderViewModel.navigationCommand.observe(this, { command ->
            when (command) {
                is NavigationCommand.To -> findNavController().navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
                is NavigationCommand.BackTo -> findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authenticationPreference = AuthenticationPreference(myContext)
        viewBinding = getFragmentBinding(inflater, container)

        return viewBinding.root
    }
    fun loggingOut() = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            authenticationPreference.clearData()
            LocalDB.deleteTables(myContext)
        }
        val intent = Intent(myContext, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity?.finish()
        startActivity(intent)
    }

}