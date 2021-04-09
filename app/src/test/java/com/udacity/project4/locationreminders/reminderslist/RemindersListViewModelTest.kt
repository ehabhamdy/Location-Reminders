package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    private val context: Application = ApplicationProvider.getApplicationContext()
    private val testReminder = ReminderDataItem("test", "test reminder", "mars", 200.0, 200.0)

    //val saveReminderViewModel: SaveReminderViewModel by inject()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        val reminder1 = ReminderDTO(
            "home",
            "my home",
            "mars",
            200.0,
            200.0
        )
        val reminder2 = ReminderDTO(
            "library",
            "University Public Library",
            "Campus",
            100.0,
            100.0
        )
        reminderDataSource = FakeDataSource()
        (reminderDataSource as FakeDataSource).addReminder(reminder1, reminder2)
        reminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @Test
    fun loadRemindersWhenRemindersAreUnavailable_showErrorSnackBar() {
        (reminderDataSource as FakeDataSource).setReturnError(true)
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), `is`("Test Exception"))
    }

    @Test
    fun loadReminders_success_returnsReminders() = runBlockingTest {
        reminderListViewModel.loadReminders()
        val reminders = reminderDataSource.getReminders() as Result.Success
        assertThat(
            reminderListViewModel.remindersList.getOrAwaitValue(),
            `is`(notNullValue())
        )

        val dataList = ArrayList<ReminderDataItem>()
        dataList.addAll(reminders.data.map { reminder ->
            //map the reminder data from the DB to the be ready to be displayed on the UI
            ReminderDataItem(
                reminder.title,
                reminder.description,
                reminder.location,
                reminder.latitude,
                reminder.longitude,
                reminder.id
            )
        })

        assertEquals(
            reminderListViewModel.remindersList.getOrAwaitValue(),
            dataList
        )

    }

    @Test
    fun loadReminders_loading() {
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }
}