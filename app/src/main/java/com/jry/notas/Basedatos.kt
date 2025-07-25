package com.jry.notas

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

/**
 Esta interfaz define las operaciones de base de datos (consultas, inserciones, etc.)
 que se pueden realizar sobre la tabla 'tasks'. Room se encarga de generar la implementación.
 */

@Dao
interface TaskDao {

     //Recupera todas las tareas de la base de datos, ordenadas por ID de forma descendente.

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>

    //Recupera una única tarea por su ID.

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Int): Flow<Task?>


     // Inserta una nueva nota.

    @Insert
    suspend fun insert(task: Task)

    // Actualiza una nota existente.

    @Update
    suspend fun updateTask(task: Task)

    // Elimina una nota.

    @Delete
    suspend fun deleteTask(task: Task)
}

/**
 Clase principal de la base de datos de la aplicación, basada en Room.
 Define la configuración de la base de datos, incluyendo las entidades (tablas) y la versión.
 Sirve como el punto de acceso principal a los datos persistentes de la app.
 */
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

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        /**
         Obtiene la instancia única (patrón Singleton) de [TaskDatabase].
         Este metodo previene la creación de múltiples instancias de la base de datos,
         lo cual es costoso. El bloque `synchronized` garantiza que la inicialización
         sea segura en entornos con múltiples hilos (thread-safe).
         */
        fun getDatabase(context: Context): TaskDatabase =
            // Si la instancia ya existe, la devuelve. Si no, la crea.
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "basedatos" // Nombre del archivo de la base de datos.
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
            }
    }
}