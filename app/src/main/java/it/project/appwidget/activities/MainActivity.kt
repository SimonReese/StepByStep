package it.project.appwidget.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.project.appwidget.R
import it.project.appwidget.fragments.Config
import it.project.appwidget.fragments.Home
import it.project.appwidget.fragments.Run
import it.project.appwidget.fragments.Setup
import it.project.appwidget.fragments.Stats

class MainActivity : AppCompatActivity() {

    private val home: Fragment = Home()
    private val run: Fragment = Run()
    private val stats: Fragment = Stats()
    private val config: Fragment = Config()
    private val setup: Fragment = Setup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Chiamato onCreate")
        setContentView(R.layout.activity_main)

        // TODO: CHIEDERE PERMESSI!!!

        val bottom_nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Imposto il fragment sul fragment manager solo se il l'activity non è stata ricreata da un altra
        if (savedInstanceState == null){
            replaceFragment(R.id.home)
        }

        bottom_nav.setOnItemSelectedListener {menuItem: MenuItem ->
            replaceFragment(menuItem.itemId)
            true
        }
    }

    private fun replaceFragment(menuId: Int){
        val transaction = supportFragmentManager.beginTransaction()
        when (menuId){
            R.id.home -> transaction.replace(R.id.fragmentContainerView, home)
            R.id.run -> transaction.replace(R.id.fragmentContainerView, run)
            R.id.stats -> transaction.replace(R.id.fragmentContainerView, stats)
            R.id.settings -> transaction.replace(R.id.fragmentContainerView, config)
            //R.id.setup -> transaction.replace(R.id.fragmentContainerView, setup)
        }
        transaction.commit()
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
