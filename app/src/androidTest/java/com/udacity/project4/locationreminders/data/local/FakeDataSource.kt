package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class FakeDataSource(
    private val remindersDao: RemindersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source

    private var shouldReturnError = false

    var reminders: MutableList<ReminderDTO>? = mutableListOf()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        // TODO("Return the reminders")
        if(shouldReturnError) {
            return Result.Error("Test Exception")
        }

        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(
            "Reminders no found", 0
        )
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // TODO("save the reminder")
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        // TODO("return the reminder with the id")
        if(shouldReturnError) {
            return Result.Error("Test Exception")
        }

        reminders?.let {
            return Result.Success(it?.filter { it.id == id }?.get(0))
        }
        return Result.Error(
            "Reminder no found", 0
        )
    }

    override suspend fun deleteAllReminders() {
        // TODO("delete all the reminders")
        reminders?.clear()
    }

    fun addReminder(vararg newReminders: ReminderDTO) {
        for (reminder in newReminders) {
            reminders?.add(reminder)
        }
    }


}