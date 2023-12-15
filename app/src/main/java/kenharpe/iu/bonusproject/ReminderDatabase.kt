package kenharpe.iu.bonusproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1)
abstract class ReminderDatabase : RoomDatabase() {
    abstract val reminderDao: ReminderDao

    /**
     * Establishes the Instance of the database object in a companion object, which works akin to the usual
     * singleton pattern seen in other languages like Java or C#, but just done a little weird in Kotlin
     */
    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null
        fun getInstance(context: Context): ReminderDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ReminderDatabase::class.java,
                        "reminders_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}