package it.project.appwidget.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import it.project.appwidget.R
import it.project.appwidget.UserPreferencesHelper

class Setup : Fragment() {
    private lateinit var preferencesHelper: UserPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = UserPreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup, container, false)

        val btnSave = view.findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener {
            if (areAllFieldsFilled()) {
                // Salvataggio dei dati qui
                val nomeUtente: String = (view.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.text ?: "").toString()
                val peso: String = (view.findViewById<TextInputLayout>(R.id.peso)?.editText?.text ?: "").toString()
                val eta: String = (view.findViewById<TextInputLayout>(R.id.eta)?.editText?.text ?: "").toString()
                val sesso: String = (view.findViewById<TextInputLayout>(R.id.sesso)?.editText?.text ?: "").toString()
                val tipologiaAttivita: String = (view.findViewById<TextInputLayout>(R.id.tipologia_attivita)?.editText?.text ?: "").toString()

                // Salvare i dati utilizzando l'helper delle preferenze condivise
                preferencesHelper.nome = nomeUtente
                preferencesHelper.peso = peso
                preferencesHelper.eta = eta
                preferencesHelper.sesso = sesso
                preferencesHelper.tipologiaAttivita = tipologiaAttivita

                // Apri il fragment "config"
                navigateToSetupFragment()

                //TODO: MANDA INTENT

            } else {
                // Mostra un messaggio di errore o un feedback all'utente
                Log.d("Setup", "Compila tutti i campi")
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateFields()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun areAllFieldsFilled(): Boolean {
        val nomeUtente: String = (view?.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.text ?: "").toString()
        val peso: String = (view?.findViewById<TextInputLayout>(R.id.peso)?.editText?.text ?: "").toString()
        val eta: String = (view?.findViewById<TextInputLayout>(R.id.eta)?.editText?.text ?: "").toString()
        val sesso: String = (view?.findViewById<TextInputLayout>(R.id.sesso)?.editText?.text ?: "").toString()
        val tipologiaAttivita: String = (view?.findViewById<TextInputLayout>(R.id.tipologia_attivita)?.editText?.text ?: "").toString()

        return nomeUtente.isNotEmpty() && peso.isNotEmpty() && eta.isNotEmpty() && sesso.isNotEmpty() && tipologiaAttivita.isNotEmpty()
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


    private fun populateFields() {
        val nomeUtente = preferencesHelper.nome
        val peso = preferencesHelper.peso
        val eta = preferencesHelper.eta
        val sesso = preferencesHelper.sesso
        val tipologiaAttivita = preferencesHelper.tipologiaAttivita

        view?.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.setText(nomeUtente)
        view?.findViewById<TextInputLayout>(R.id.peso)?.editText?.setText(peso)
        view?.findViewById<TextInputLayout>(R.id.eta)?.editText?.setText(eta)
        view?.findViewById<TextInputLayout>(R.id.sesso)?.editText?.setText(sesso)
        view?.findViewById<TextInputLayout>(R.id.tipologia_attivita)?.editText?.setText(tipologiaAttivita)
    }


}

