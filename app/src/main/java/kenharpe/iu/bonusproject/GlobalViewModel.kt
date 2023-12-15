package kenharpe.iu.bonusproject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date


class GlobalViewModel : ViewModel()
{
    private lateinit var dao: ReminderDao

    val _reminders = MutableLiveData(mutableListOf<Reminder>())
    val reminders : LiveData<List<Reminder>> get() = _reminders as LiveData<List<Reminder>>

    private val _navigateToList = MutableLiveData<Boolean>()
    val navigateToList : LiveData<Boolean> get() = _navigateToList

    private val _navigateToReminder = MutableLiveData<Long?>(null)
    val navigateToReminder : LiveData<Long?> get() = _navigateToReminder

    private val _reminderTitle = MutableLiveData<String>()
    val reminderTitle : LiveData<String> get() = _reminderTitle

    private val _reminderDescription = MutableLiveData<String>()
    val reminderDescription : LiveData<String> get() = _reminderDescription

    private val _reminderDate = MutableLiveData<LocalDate>()
    val reminderDate : LiveData<LocalDate> get() = _reminderDate

    private val _reminderTime = MutableLiveData<LocalTime>()
    val reminderTime : LiveData<LocalTime> get() = _reminderTime

    private var currentReminderID = 0L
    var isNewReminder = false
    var isInitialized = false

    fun setDao(newDao: ReminderDao)
    {
        dao = newDao
        isInitialized = true
    }

    fun loadReminders()
    {
        val storedReminders = dao.getAll()
        Log.v("GlobalViewModel", "loadReminders called!")
        _reminders.value = dao.getAll().value as MutableList<Reminder>?
        if (storedReminders.isInitialized)
        {
            for (reminder in storedReminders.value!!)
            {
                Log.v("GlobalViewModel", "Title:${reminder.title}, Description:${reminder.description}, Time:${LocalDateTime.ofInstant(Instant.ofEpochMilli(reminder.time), ZoneId.systemDefault())}")
            }
        }

    }

    fun goToList()
    {
        _navigateToList.postValue(true)
    }

    fun onListNavigated()
    {
        _navigateToList.postValue(false)
    }

    fun newReminder()
    {
        _navigateToReminder.postValue(0L)
        isNewReminder = true
    }

    fun onReminderClicked(reminderId: Long)
    {
        _navigateToReminder.postValue(reminderId)
        currentReminderID = reminderId
        var currentReminderLiveData = dao.get(reminderId)
        val currentReminder = currentReminderLiveData.value!!
        _reminderTitle.value = currentReminder.title
        _reminderDescription.value = currentReminder.description
        val reminderInstant = Instant.ofEpochMilli(currentReminder.time)
        val reminderDateTime = LocalDateTime.ofInstant(reminderInstant, ZoneId.systemDefault())
        _reminderDate.value = reminderDateTime.toLocalDate()
        _reminderTime.value = reminderDateTime.toLocalTime()
        isNewReminder = false
    }


    fun updateTitle(text: String)
    {
        _reminderTitle.postValue(text)
    }

    fun updateDescription(text: String)
    {
        _reminderDescription.postValue(text)
    }

    fun updateDate(year: Int, month: Int, day: Int)
    {
        val currentDate = LocalDate.of(year, month, day)
        _reminderDate.postValue(currentDate)
    }

    fun updateTime(hour: Int, second: Int)
    {
        val currentTime = LocalTime.of(hour, second)
        _reminderTime.postValue(currentTime)
    }

    fun saveReminder()
    {
        Log.v("GlobalViewModel", "SaveReminder Called")
        if (reminderTitle.value != null && reminderTime.value != null && reminderDate.value != null)
        {
            viewModelScope.launch {
                val reminder = Reminder()
                reminder.reminderID = currentReminderID
                reminder.title = reminderTitle.value!!
                reminder.description = reminderDescription.value ?: ""
                reminder.time = LocalDateTime.of(reminderDate.value, reminderTime.value).toInstant(ZoneOffset.UTC).toEpochMilli()
                dao.insert(reminder)
                Log.v("GlobalViewModel", "Title:${reminder.title}, Description:${reminder.description}, Time:${LocalDateTime.ofInstant(Instant.ofEpochMilli(reminder.time), ZoneId.systemDefault())}")
            }.invokeOnCompletion { Log.v("GlobalViewModel", "Reminder Inserted") }
        }
    }

    fun deleteReminder(reminderId: Long)
    {
        val delReminder : Reminder = reminders.value?.first { (reminderId == it.reminderID) }!!
        viewModelScope.launch {
            dao.delete(delReminder)
        }
    }
}