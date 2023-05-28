package it.project.appwidget.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import it.project.appwidget.R
import it.project.appwidget.UserPreferencesHelper


class Config : Fragment() {

    private lateinit var modifiedDataButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_config, container, false)

        modifiedDataButton = view.findViewById(R.id.modified_data_btn)
        modifiedDataButton.setOnClickListener {
            // Apri il fragment "setup"
            navigateToSetupFragment()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza preferencesHelper
        val preferencesHelper = UserPreferencesHelper(requireContext())

        // Ottieni i dati di setup
        val nomeUtente = preferencesHelper.nome
        val peso = preferencesHelper.peso
        val eta = preferencesHelper.eta
        val sesso = preferencesHelper.sesso
        val kcalTarget = preferencesHelper.kcalTarget

        // Imposta i valori nei TextView corrispondenti
        view.findViewById<TextView>(R.id.valore_nome).text = nomeUtente
        view.findViewById<TextView>(R.id.valore_peso).text = peso
        view.findViewById<TextView>(R.id.valore_eta).text = eta
        view.findViewById<TextView>(R.id.valore_sesso).text = sesso
        view.findViewById<TextView>(R.id.valore_kcal).text = kcalTarget
    }



    private fun navigateToSetupFragment() {
        // Ottieni il riferimento al controllore della navigazione dall'activity
        val navController = activity?.run {
            findNavController(R.id.navigationHostFragment)
        }

        // Effettua la navigazione verso il fragment "setup"
        navController?.navigate(R.id.setup)
    }


}