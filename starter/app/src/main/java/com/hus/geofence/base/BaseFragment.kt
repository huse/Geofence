package com.hus.geofence.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.hus.geofence.authentication.AuthenticationActivity
import com.hus.geofence.authentication.AuthenticationPreference
import com.hus.geofence.authentication.AuthenticationViewModelFactory
import com.hus.geofence.locationreminders.data.local.LocalDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Base Fragment to observe on the common LiveData objects
 */
abstract class BaseFragment<baseViewModel: BaseViewModel, viewBinding: ViewBinding, fundationRepo: FundationRepo> : Fragment()  {
    /**
     * Every fragment has to have an instance of a view model that extends from the BaseViewModel
     */

    protected lateinit var authenticationPreference: AuthenticationPreference
    protected lateinit var binding : viewBinding
    protected lateinit var _viewModel : baseViewModel
    protected lateinit var myContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authenticationPreference = AuthenticationPreference(myContext)
        binding = getFragmentBinding(inflater, container)
        val factory = AuthenticationViewModelFactory(getFragmentRepository())
        _viewModel = ViewModelProvider(this, factory).get(getViewModel())

        return binding.root
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

    abstract fun getViewModel() : Class<baseViewModel>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : viewBinding

    abstract fun getFragmentRepository(): fundationRepo
    override fun onStart() {
        super.onStart()
        _viewModel.showErrorMessage.observe(this, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        _viewModel.showToast.observe(this, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        _viewModel.showSnackBar.observe(this, Observer {
            Snackbar.make(this.view!!, it, Snackbar.LENGTH_LONG).show()
        })
        _viewModel.showSnackBarInt.observe(this, Observer {
            Snackbar.make(this.view!!, getString(it), Snackbar.LENGTH_LONG).show()
        })

        _viewModel.navigationCommand.observe(this, Observer { command ->
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
}