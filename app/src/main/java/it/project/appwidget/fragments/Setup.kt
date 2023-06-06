package it.project.appwidget.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

        //Bottone salva
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener {
            if (areAllFieldsFilled()) {
                // Salvataggio dei dati
                val nomeUtente: String = (view.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.text ?: "").toString()
                val peso: String = (view.findViewById<TextInputLayout>(R.id.peso)?.editText?.text ?: "").toString()
                val eta: String = (view.findViewById<TextInputLayout>(R.id.eta)?.editText?.text ?: "").toString()
                val sesso: String = (view.findViewById<TextInputLayout>(R.id.sesso)?.editText?.text ?: "").toString()
                val kcalTarget: String = (view.findViewById<TextInputLayout>(R.id.kcalTarget)?.editText?.text ?: "").toString()

                // Salva i dati utilizzando l'helper nelle preferenze condivise
                preferencesHelper.nome = nomeUtente
                preferencesHelper.peso = peso
                preferencesHelper.eta = eta
                preferencesHelper.sesso = sesso
                preferencesHelper.kcalTarget = kcalTarget.toInt()

                // Apri il fragment "config"
                navigateToSetupFragment()

            } else {
                //Creo finestra di dialogo con l'utente
                val alert = AlertDialog.Builder(context)
                    .setTitle("Inserimento errato")
                    .setMessage("Non hai compilato correttamente i campi")
                    .setIcon(R.drawable.icons8_errore_24)
                    .setPositiveButton("Ritorna alla pagina inserimento"){_, _ ->

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
        val sesso: String = (view?.findViewById<TextInputLayout>(R.id.sesso)?.editText?.text ?: "").toString()
        val kcalTarget: String = (view?.findViewById<TextInputLayout>(R.id.kcalTarget)?.editText?.text ?: "").toString()

        return nomeUtente.isNotEmpty() && peso.isNotEmpty() && eta.isNotEmpty() && sesso.isNotEmpty() && kcalTarget.isNotEmpty()
    }

    // Riempi TextInputLayout con i dati precedentemente inseriti
    private fun populateFields() {
        val nomeUtente = preferencesHelper.nome
        val peso = preferencesHelper.peso
        val eta = preferencesHelper.eta
        val sesso = preferencesHelper.sesso
        val kcalTarget = preferencesHelper.kcalTarget

        view?.findViewById<TextInputLayout>(R.id.nome_utente)?.editText?.setText(nomeUtente)
        view?.findViewById<TextInputLayout>(R.id.peso)?.editText?.setText(peso)
        view?.findViewById<TextInputLayout>(R.id.eta)?.editText?.setText(eta)
        view?.findViewById<TextInputLayout>(R.id.sesso)?.editText?.setText(sesso)
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

