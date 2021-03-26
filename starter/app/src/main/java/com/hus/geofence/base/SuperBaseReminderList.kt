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
import com.hus.geofence.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

abstract class SuperBaseReminderList<viewBinding: ViewBinding> : Fragment() {

    protected lateinit var authenticationPreference: AuthenticationPreference
    protected lateinit var viewBinding : viewBinding
    protected lateinit var myContext: Context



    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : viewBinding

    override fun onStart() {
        super.onStart()
        remindersListViewModel.showErrorMessage.observe(this, {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        remindersListViewModel.showToast.observe(this, {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        remindersListViewModel.showSnackBar.observe(this, {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })
        remindersListViewModel.showSnackBarInt.observe(this, {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })

        remindersListViewModel.navigationCommand.observe(this, { command ->
            when (command) {
                is NavigationCommand.To -> this.findNavController().navigate(command.directions)
                is NavigationCommand.Back -> this.findNavController().popBackStack()
                is NavigationCommand.BackTo -> this.findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }
        })
    }
    protected val remindersListViewModel: RemindersListViewModel by inject()

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