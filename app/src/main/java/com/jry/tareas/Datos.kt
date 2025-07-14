package com.jry.tareas

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

/**
 Clase para gestionar el acceso a los datos guardados
 Proporciona una forma segura y asíncrona de guardar y leer la configuración
 del modo oscuro de la aplicación.
 */
class Datos(private val context: Context) {

    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->

            preferences[DARK_MODE_KEY] == true
        }

    suspend fun guardarModoOscuro(isDarkMode: Boolean) {
        context.dataStore.edit { settings ->
            settings[DARK_MODE_KEY] = isDarkMode
        }
    }
}
