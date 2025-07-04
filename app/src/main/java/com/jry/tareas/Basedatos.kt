package com.jry.tareas

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert suspend fun insertTask(task: Task)
    @Update suspend fun updateTask(task: Task)
    @Query("SELECT * FROM tasks") fun getAllTasks(): Flow<List<Task>>
    @Query("SELECT * FROM tasks WHERE id = :taskId") fun getTaskById(taskId: Int): Flow<Task?>
    @Delete suspend fun deleteTask(task: Task)
}

@Database(
    entities = [Task::class],
    version = 2,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN colorHex TEXT")
            }
        }

        @Volatile private var INSTANCE: TaskDatabase? = null
        fun getDatabase(context: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
        }
    }
}