package com.jry.tareas

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert suspend fun insertTask(task: Task)
    @Query("SELECT * FROM tasks") fun getAllTasks(): Flow<List<Task>>
    @Query("SELECT * FROM tasks WHERE id = :taskId") fun getTaskById(taskId: Int): Flow<Task>
    @Delete suspend fun deleteTask(task: Task)
}

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object {
        @Volatile private var INSTANCE: TaskDatabase? = null
        fun getDatabase(context: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context, TaskDatabase::class.java, "task_database").build().also { INSTANCE = it }
        }
    }
}