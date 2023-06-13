package it.project.appwidget.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import it.project.appwidget.R
import it.project.appwidget.UserPreferencesHelper

class Setup : Fragment() {
    private lateinit var preferencesHelper: UserPreferencesHelper
    private lateinit var genderSpinner: Spinner
    private var positionSelected: Int = -1

    /** elemento selezionato della lista*/
    private lateinit var genderItem: String

    /** Listener che si aggiorna quando seleziono un elemento diverso della lista*/
    private val itemSelectedListener = ItemSelectedListener()


    /** classe per la gestione dei click nello Spinner */
    private inner class ItemSelectedListener: AdapterView.OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            genderItem = parent?.getItemAtPosition(position) as String
            positionSelected = position
            Log.d("posizione Spinner", "$positionSelected")
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //se non seleziono nulla prendo l'item di default (cioè M)
            return
        }


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = UserPreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup, container, false)
        //Recupero lo spinner
        val genderSpinner = view.findViewById<Spinner>(R.id.gender_Spinner)
        // Imposto dati spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.gender_configure_options, R.layout.gender_spinner_item)
        spinnerAdapter.setDropDownViewResource(R.layout.gender_spinner_item)
        // Inizializzo item selezionato
        genderItem = spinnerAdapter.getItem(0) as String
        // Imposto adapter e listener sullo spinner
        genderSpinner.adapter = spinnerAdapter
        genderSpinner.onItemSelectedListener = itemSelectedListener
        //Bottone salva
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener {
            if (areAllFieldsFilled()) {
                // Salvataggio dei dati
                val nomeUtente: String = (view.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.text ?: "").toString()
                val peso: String = (view.findViewById<TextInputLayout>(R.id.peso)?.editText?.text ?: "").toString()
                val eta: String = (view.findViewById<TextInputLayout>(R.id.eta)?.editText?.text ?: "").toString()
                val sesso: String = genderItem
                val kcalTarget: String = (view.findViewById<TextInputLayout>(R.id.kcalTarget)?.editText?.text ?: "").toString()

                // Salva i dati utilizzando l'helper nelle preferenze condivise
                preferencesHelper.nome = nomeUtente
                preferencesHelper.peso = peso
                preferencesHelper.eta = eta
                preferencesHelper.sesso = sesso
                preferencesHelper.kcalTarget = kcalTarget.toInt()

                preferencesHelper.spinnerPosition = positionSelected

                // Apri il fragment "config"
                navigateToSetupFragment()

            } else {
                //Creo finestra di dialogo con l'utente
                val alert = AlertDialog.Builder(context)
                    .setTitle("Inserimento errato")
                    .setMessage("Non hai compilato correttamente i campi")
                    .setIcon(R.drawable.icons8_errore_24)
                    .setPositiveButton("OK"){_, _ ->

                    }
                alert.show()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateFields()
        super.onViewCreated(view, savedInstanceState)
    }

    //Controlla che tutti i campi siano riempiti

    private fun areAllFieldsFilled(): Boolean {
        val nomeUtente: String = (view?.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.text ?: "").toString()
        val peso: String = (view?.findViewById<TextInputLayout>(R.id.peso)?.editText?.text ?: "").toString()
        val eta: String = (view?.findViewById<TextInputLayout>(R.id.eta)?.editText?.text ?: "").toString()
        val kcalTarget: String = (view?.findViewById<TextInputLayout>(R.id.kcalTarget)?.editText?.text ?: "").toString()

        return nomeUtente.isNotEmpty() && peso.isNotEmpty() && eta.isNotEmpty() && kcalTarget.isNotEmpty()
    }

    // Riempi TextInputLayout con i dati precedentemente inseriti
    private fun populateFields() {
        val nomeUtente = preferencesHelper.nome
        val peso = preferencesHelper.peso
        val eta = preferencesHelper.eta
        val sesso = preferencesHelper.sesso
        val kcalTarget = preferencesHelper.kcalTarget
        val positionSelected = preferencesHelper.spinnerPosition


        view?.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.setText(nomeUtente)
        view?.findViewById<TextInputLayout>(R.id.peso)?.editText?.setText(peso)
        view?.findViewById<TextInputLayout>(R.id.eta)?.editText?.setText(eta)
        view?.findViewById<Spinner>(R.id.gender_Spinner)?.setSelection(positionSelected)
        view?.findViewById<TextInputLayout>(R.id.kcalTarget)?.editText?.setText(kcalTarget.toString())
    }

    private fun navigateToSetupFragment() {
        // Ottieni il riferimento al controllore della navigazione dall'activity
        val navController = activity?.run {
            findNavController(R.id.navigationHostFragment)
        }

        // Effettua la navigazione verso il fragment "config" e aggiungi "Setup" al back stack
        navController?.navigate(R.id.config)
        navController?.popBackStack(R.id.setup, true)
    }


}

