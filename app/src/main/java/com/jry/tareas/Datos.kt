package com.jry.tareas

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")
class Datos(context: Context) {
    private val ds = context.dataStore
    companion object { val DARK = booleanPreferencesKey("dark_mode") }
    val darkMode = ds.data.map { it[DARK] == true }
    suspend fun guardarModoOscuro(d: Boolean) { ds.edit { it[DARK] = d } }
}
