package it.project.appwidget.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private var currentFragmentId: Int = -1

    val home = Home()
    val run = Run()
    val stats = Stats()
    val config = Config()
    val setup = Setup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO: CHIEDERE PERMESSI!!!
        val bottom_nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        replaceFragment(R.id.home)
        currentFragmentId = 1
        restoreState(savedInstanceState)

        bottom_nav.setOnItemSelectedListener {menuItem: MenuItem ->
            replaceFragment(menuItem.itemId)
            true
        }


    }

    private fun replaceFragment(menuId: Int){
        val transaction = supportFragmentManager.beginTransaction()
        when (menuId){
            R.id.home -> transaction.replace(R.id.frame_layout, home)
            R.id.run -> transaction.replace(R.id.frame_layout, run)
            R.id.stats -> transaction.replace(R.id.frame_layout, stats)
            R.id.settings -> transaction.replace(R.id.frame_layout, config)
            //R.id.setup -> transaction.replace(R.id.frame_layout, setup)
        }
        transaction.commit()
        currentFragmentId = menuId
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("currentFragmentId", currentFragmentId)
        super.onSaveInstanceState(outState)
    }

    private fun restoreState(inState: Bundle?){
        if (inState == null)
            return
        val previous = inState.getInt("currentFragmentId")
        // TODO: Pulire reimpostazione inutile
        replaceFragment(previous)
    }




}
