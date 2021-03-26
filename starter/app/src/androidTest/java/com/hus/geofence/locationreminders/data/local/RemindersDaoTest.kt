package com.hus.geofence.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.hus.geofence.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var remindersDao: RemindersDao
    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var reminderDTO: ReminderDTO
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun beforeTestStart() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        reminderDTO = facebookLocation()
        remindersDao = remindersDatabase.reminderDao()
    }
    @Test
    fun emptyList1() = runBlockingTest {
        remindersDao.saveReminder(reminderDTO)
        MatcherAssert.assertThat(remindersDao.getReminders(), CoreMatchers.hasItem(reminderDTO))
        remindersDao.deleteAllReminders()
        MatcherAssert.assertThat(remindersDao.getReminders().isEmpty(), CoreMatchers.`is`(true))
    }
    fun facebookLocation(): ReminderDTO {
        return ReminderDTO(
            title = "FaceBook",
            description = "Building",
            location = "Newyork",
            latitude = 40.73090,
            longitude = -73.99207
        )
    }

}