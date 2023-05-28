package it.project.appwidget

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesHelper(context: Context) {
    companion object {
        private const val PREFS_FILE_NAME = "UserPreferences"
        private const val KEY_NOME_UTENTE = "nome_utente"
        private const val KEY_PESO = "peso"
        private const val KEY_ETA = "eta"
        private const val KEY_SESSO = "sesso"
        private const val KEY_TIPOLOGIA_ATTIVITA = "tipologia_attivita"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

    var nome: String
        get() = sharedPreferences.getString(KEY_NOME_UTENTE, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_NOME_UTENTE, value).apply()

    var peso: String
        get() = sharedPreferences.getString(KEY_PESO, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_PESO, value).apply()

    var eta: String
        get() = sharedPreferences.getString(KEY_ETA, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_ETA, value).apply()

    var sesso: String
        get() = sharedPreferences.getString(KEY_SESSO, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_SESSO, value).apply()

    var tipologiaAttivita: String
        get() = sharedPreferences.getString(KEY_TIPOLOGIA_ATTIVITA, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_TIPOLOGIA_ATTIVITA, value).apply()
}
