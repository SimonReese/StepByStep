package it.project.appwidget.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import it.project.appwidget.R
import it.project.appwidget.databinding.GraphWidgetConfigureBinding
import it.project.appwidget.widgets.*
import kotlin.properties.Delegates


class GraphWidgetConfigureActivity : AppCompatActivity(){

    private lateinit var saveButton: Button
    private lateinit var optionSpinner: Spinner

    // Stato
    protected lateinit var selectedItem: String
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_widget_configure)

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


        saveButton.setOnClickListener {
            Log.d("GraphWidgetConfigureActivity", "Selezionato $selectedItem su $widgetId")
            finish()
        }
    }


}





/**
 * The configuration screen for the [it.project.appwidget.widgets.GraphWidget] AppWidget.
 */
/*
class GraphWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var appWidgetText: EditText
    private var onClickListener = View.OnClickListener {
        val context = this@GraphWidgetConfigureActivity

        // When the button is clicked, store the string locally
        val widgetText = appWidgetText.text.toString()
        saveTitlePref(context, appWidgetId, widgetText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        //updateAppWidget(context, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
    private lateinit var binding: GraphWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = GraphWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //appWidgetText = binding.appwidgetText
        binding.addButton.setOnClickListener(onClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        appWidgetText.setText(loadTitlePref(this@GraphWidgetConfigureActivity, appWidgetId))
    }

}


// TODO: Capire e rimuovere qua sotto
private const val PREFS_NAME = "it.project.appwidget.widgets.GraphWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

// Write the prefix to the SharedPreferences object for this widget
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
    return titleValue ?: context.getString(R.string.appwidget_text)
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}
*/