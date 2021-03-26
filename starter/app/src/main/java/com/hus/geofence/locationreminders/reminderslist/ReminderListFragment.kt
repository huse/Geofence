package com.hus.geofence.locationreminders.reminderslist

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.hus.geofence.R
import com.hus.geofence.authentication.AuthenticationPreference
import com.hus.geofence.base.NavigationCommand
import com.hus.geofence.base.SuperBaseReminderList
import com.hus.geofence.databinding.FragmentRemindersBinding
import com.hus.geofence.locationreminders.data.local.LocalDB
import com.hus.geofence.locationreminders.data.local.RemindersDao
import com.hus.geofence.utils.setDisplayHomeAsUpEnabled
import com.hus.geofence.utils.setTitle
import com.hus.geofence.utils.setup

class ReminderListFragment : SuperBaseReminderList<FragmentRemindersBinding>() {
    //use Koin to retrieve the ViewModel instance
    private lateinit var remindersDao: RemindersDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myContext = requireContext()
        remindersDao = LocalDB.createRemindersDao(myContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext = requireContext()
        authenticationPreference = AuthenticationPreference(myContext)
        viewBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        viewBinding.viewModel = remindersListViewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        viewBinding.refreshLayout.setOnRefreshListener { remindersListViewModel.loadReminders() }

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.viewModel = remindersListViewModel
        setTitle(getString(R.string.app_name))
        viewBinding.refreshLayout.setOnRefreshListener { remindersListViewModel.loadReminders() }
        viewBinding.lifecycleOwner = this
        setupRecyclerView()
        viewBinding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        remindersListViewModel.loadReminders()
    }

    private fun navigateToAddReminder(reminder: ReminderDataItem? = null) {
        //use the navigationCommand live data to navigate between the fragments
        remindersListViewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
            navigateToAddReminder(it)
        }

//        setup the recycler view using the extension function
        viewBinding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance()
                    .signOut(myContext)
                    .addOnCompleteListener {
                        loggingOut()
                    }
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRemindersBinding.inflate(inflater, container, false)

}
