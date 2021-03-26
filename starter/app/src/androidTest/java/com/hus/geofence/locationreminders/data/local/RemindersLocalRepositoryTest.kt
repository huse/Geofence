package com.hus.geofence.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.hus.geofence.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith
import com.hus.geofence.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import org.junit.Before

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt
private lateinit var remindersLocalRepository: RemindersLocalRepository
private lateinit var reminderDTO: ReminderDTO
private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun beforeTestStart() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository = RemindersLocalRepository(remindersDatabase.reminderDao(), Dispatchers.Main)
        reminderDTO = facebookLocation()
    }

    @Test
    fun emptyList2() = runBlocking {
        remindersLocalRepository.saveReminder(reminderDTO)
        var reminders = remindersLocalRepository.getReminders() as Result.Success
        MatcherAssert.assertThat(reminders.data, CoreMatchers.hasItem(reminderDTO))
        remindersLocalRepository.deleteAllReminders()
        reminders = remindersLocalRepository.getReminders() as Result.Success
        MatcherAssert.assertThat(reminders.data.isEmpty(), CoreMatchers.`is`(true))
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