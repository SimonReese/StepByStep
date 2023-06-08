package it.project.appwidget.activities

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigatorExtras
import it.project.appwidget.R
import it.project.appwidget.widgets.*
import kotlinx.coroutines.launch

/**
 * Classe che gestisce l'activity di configurazione del [GraphWidget].
 *
 * Permette all'utente di scegliere un campo da graficare tra distanza, calorie e durata delle sessioni
 */
class GraphWidgetConfigureActivity : AppCompatActivity(){

    private lateinit var saveButton: Button
    private lateinit var optionSpinner: Spinner

    // Stato
    private lateinit var selectedItem: String
    private var widgetId = -1

    // Listener
    private val itemSelectedListener = ItemSelectedListener()

    // Implementazione listener
    private inner class ItemSelectedListener: AdapterView.OnItemSelectedListener {

        // Viene chiamato anche quando l'activity viene ruotata
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d("GraphWidgetConfigureActivity", "Selezionato item")
            selectedItem = parent?.getItemAtPosition(position) as String
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            return
        }

    }

    // Creazione activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_widget_configure)

        // Imposto risultato fallito nel caso in cui l'utente esca senza salvare
        setResult(RESULT_CANCELED)

        // Recupero widgetId
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

        // Ottengo riferimenti a views
        saveButton = findViewById(R.id.graphWidgetSaveButton)
        optionSpinner = findViewById(R.id.graphWidgetOption)

        // Imposto dati spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.graph_widget_configure_options, R.layout.graph_widget_spinner_item)
        spinnerAdapter.setDropDownViewResource(R.layout.graph_widget_spinner_item)
        // Inizializzo item selezionato
        selectedItem = spinnerAdapter.getItem(0) as String
        // Imposto adapter e listener sullo spinner
        optionSpinner.adapter = spinnerAdapter
        optionSpinner.onItemSelectedListener = itemSelectedListener

        // Imposto listener sul bottone
        saveButton.setOnClickListener {
            Log.d("GraphWidgetConfigureActivity", "Selezionato $selectedItem su $widgetId")
            saveAndUpdate()
            setResult(RESULT_OK)
            finish()
        }
    }


    // Salvo impostazione spinner su Shared Preferences e aggiorno il widget in background
    private fun saveAndUpdate(){
        lifecycleScope.launch {
            // Salvo su sharedprefs
            val prefs = getSharedPreferences(GraphWidget.SHARED_PREFERENCES_FILE_PREFIX + widgetId, MODE_PRIVATE).edit()
            prefs.putString(GraphWidget.DATA_SETTINGS, selectedItem)
            prefs.apply()

            // Aggiorno widget
            // Ottengo istanza AppWidgetMananger
            val appWidgetManager = AppWidgetManager.getInstance(this@GraphWidgetConfigureActivity)
            GraphWidget.updateWidget(this@GraphWidgetConfigureActivity, appWidgetManager, widgetId)
        }
    }


}