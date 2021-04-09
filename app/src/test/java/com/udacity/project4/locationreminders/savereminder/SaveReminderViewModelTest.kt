package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects
    // Subject under test
    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

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
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @Test
    fun saveReminder_showsReminderSavedToast() {
        saveReminderViewModel.saveReminder(testReminder)
        assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(),
            `is`(context.getString(R.string.reminder_saved))
        )
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }


    @Test
    fun saveReminder_navigatesBack() {
        saveReminderViewModel.saveReminder(testReminder)
        assertEquals(
            saveReminderViewModel.navigationCommand.getOrAwaitValue(),
            NavigationCommand.Back
        )
    }

    @Test
    fun validateEnteredData_returnsTrue() {
        assertTrue(saveReminderViewModel.validateEnteredData(testReminder))
    }

    @Test
    fun validateEnteredData_nullReminderTitle_returnTitleErrorMessage() {
        val invalidReminder = ReminderDataItem(
            null,
            "library",
            "University Public Library",
            100.0,
            100.0
        )
        saveReminderViewModel.validateEnteredData(invalidReminder)

        assertEquals(
            context.getString(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()),
            context.getString(R.string.err_enter_title)
        )
    }

    @Test
    fun validateEnteredData_nullReminderLocation_returnLocationErrorMessage() {
        val invalidReminder = ReminderDataItem(
            "library",
            "library",
            null,
            100.0,
            100.0
        )
        saveReminderViewModel.validateEnteredData(invalidReminder)

        assertEquals(
            context.getString(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()),
            context.getString(R.string.err_select_location)
        )
    }

    @Test
    fun onClear_cleanViewModel() {
        saveReminderViewModel.onClear()

        assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
            `is`(nullValue())
        )
    }

    @Test
    fun saveReminder_loading() {
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(testReminder)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }
}