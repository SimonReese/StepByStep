package it.project.appwidget.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.project.appwidget.R
import it.project.appwidget.fragments.Config
import it.project.appwidget.fragments.Home
import it.project.appwidget.fragments.Run
import it.project.appwidget.fragments.Setup
import it.project.appwidget.fragments.Stats

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val home = Home()
        val setup = Setup()
        val run = Run()
        val stats = Stats()
        val config = Config()


        val bottom_nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        replaceFragment(home)

        bottom_nav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(home)
                R.id.run -> replaceFragment(run)
                R.id.stats -> replaceFragment(stats)
                R.id.settings -> replaceFragment(config)

            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.commit()
        }
    }


}
