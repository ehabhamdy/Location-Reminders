package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //    TODO: Add testing implementation to the RemindersDao.kt
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO(
            "home",
            "my home",
            "mars",
            200.0,
            200.0
        )

        database.reminderDao().saveReminder(reminder)

        // 2. WHEN - Get the reminder from the database
        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values
        assertThat(loadedReminder, notNullValue())
        assertThat(reminder.id, `is`(reminder.id))
        assertThat(reminder.title, `is`(reminder.title))
        assertThat(reminder.description, `is`(reminder.description))
        assertThat(reminder.location, `is`(reminder.location))
        assertThat(reminder.latitude, `is`(reminder.latitude))
        assertThat(reminder.longitude, `is`(reminder.longitude))
    }

    @Test
    fun deleteAllRemindersAndCheckIfDatabaseIsEmpty() = runBlockingTest {
        // 1. Insert a reminder into the DAO.
        val reminder = ReminderDTO(
            "home",
            "my home",
            "mars",
            200.0,
            200.0
        )

        database.reminderDao().saveReminder(reminder)

        // 2. Delete all reminders.
        database.reminderDao().deleteAllReminders()

        // 3. Get reminders and check if there is no data returned.
        val loadedReminder = database.reminderDao().getReminders().toList()
        val emptyList = emptyList<ReminderDTO>()
        assertThat(loadedReminder, `is`(emptyList))
    }

}