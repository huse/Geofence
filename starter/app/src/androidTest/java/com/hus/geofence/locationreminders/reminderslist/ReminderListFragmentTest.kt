package com.hus.geofence.locationreminders.reminderslist

import android.app.Application
import android.view.Gravity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.hus.geofence.R
import com.hus.geofence.locationreminders.RemindersActivity
import com.hus.geofence.locationreminders.data.local.RemindersLocalRepository
import com.hus.geofence.util.DataBindingIdlingResource
import com.hus.geofence.util.monitorActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun beforeRunningTest() {
        IdlingRegistry.getInstance().apply {
            register(dataBindingIdlingResource)
        }
    }
    @Test
    fun navigationOfFragment() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        Espresso.onView(withId(R.id.selectLocationFragment)).perform(ViewActions.click())
        activityScenario.close()
    }


}