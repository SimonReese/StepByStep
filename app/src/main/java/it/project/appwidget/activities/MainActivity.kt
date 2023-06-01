package it.project.appwidget.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.project.appwidget.R


class MainActivity : AppCompatActivity() {

    // Variabile per gestione dei permessi
    private var hasPermissions: Boolean = false

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

        // Controllo permessi
        hasPermissions = true // Devo supporla vera, perchè non è detto che onRequestPermissionsResult() sia stato chiamato
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED && Build.VERSION.SDK_INT >= 33){
            //TODO: analizzare permesso POST_NOTIFICATION (pare che sia introdotto da android 13, cosa fare nel 12)?
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {POST_NOTIFICATIONS} non concesso")
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {ACCESS_COARSE_LOCATION} non concesso")
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {ACCESS_FINE_LOCATION} non concesso")
        }

        if (!hasPermissions){
            // Chiedo tutti i permessi un una volta sola
            //TODO testare cosa succede se un permesso viene negato dalle impostazioni (i permessi concessi vengono chiesti nuovamente?)
            requestPermissions(arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), 1)
            Log.d("ServiceMonitorActivity", "Non sono stati concessi tutti i permessi necessari")
            return
        }
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
