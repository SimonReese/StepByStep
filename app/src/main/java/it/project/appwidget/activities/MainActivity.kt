package it.project.appwidget.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.project.appwidget.R
import it.project.appwidget.fragments.Config
import it.project.appwidget.fragments.Home
import it.project.appwidget.fragments.Run
import it.project.appwidget.fragments.Setup
import it.project.appwidget.fragments.Stats

class MainActivity : AppCompatActivity() {

    // Il riferimento alla view che ospiterà i vari fragment
    private lateinit var navigationHostFragment: NavHostFragment

    // Il riferiemento al controllore che si occupa della navigazione in questa activity
    private lateinit var navigationController: NavController

    // Il riferimento alla bottomNavigationBar
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Chiamato onCreate")
        setContentView(R.layout.activity_main)

        // Ottengo riferimento al navigationHostFragment tramite il supportFragmentManager
        navigationHostFragment = supportFragmentManager.findFragmentById(R.id.navigationHostFragment) as NavHostFragment
        // Dal navigationHostFragment ottengo il controllore della navigation
        navigationController = navigationHostFragment.navController

        // Ottengo riferimento alla bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        /* Aggangio le action della bottom navigation view al controller che si occupa della navigation.
         Poichè gli id del menù sono gli stessi id dei vari fragments nel grafo di navigazione, il controller
         collega autmaticamente i click sugli elementi della barra (definiti nel menù) al fragment corrispondente*/
        bottomNavigationView.setupWithNavController(navigationController)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("MainActivity", "Chiamato onSaveInstanceState")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "Chiamato onDestroy")
    }




}
