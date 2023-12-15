package kenharpe.iu.bonusproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Date

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    var reminderID: Long = 0L,
    @ColumnInfo(name = "reminder_title")
    var title: String = "",
    @ColumnInfo(name = "reminder_description")
    var description: String = "",
    @ColumnInfo(name= "reminder_time")
    var time: Long = 0L
)