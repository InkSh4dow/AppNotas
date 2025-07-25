package com.jry.notas

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 Representa una única nota en la aplicación
  Esta es una clase de datos que sirve como modelo para una nota y también como
 una entidad para la base de datos Room
 */

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val colorHex: String? = null
)