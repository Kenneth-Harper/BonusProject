package kenharpe.iu.bonusproject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)
    @Update
    suspend fun update(reminder: Reminder)
    @Delete
    suspend fun delete(reminder: Reminder)
    @Query("SELECT * FROM reminder_table WHERE reminderID LIKE :key")
    fun get(key: Long): LiveData<Reminder>
    @Query("SELECT * FROM reminder_table ORDER BY reminderID DESC")
    fun getAll(): LiveData<List<Reminder>>
}