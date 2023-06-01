package it.project.appwidget.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Switch
import it.project.appwidget.R

class Setup : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_setup, container, false)

        val switchBtn = rootView.findViewById<Switch>(R.id.switcher)

        //prelevo il frame layout
        val layout_container = rootView.findViewById<FrameLayout>(R.id.frame_layout)

        switchBtn.setOnClickListener {
            if (switchBtn.isChecked) {
                Log.i("Switch Setup", "IsChecked = true")
                layout_container.removeAllViews()
                val layout1 = inflater.inflate(R.layout.config_prova, layout_container, false)
                layout_container.addView(layout1)
            }else{
                Log.i("Switch Setup", "IsChecked = false")
                layout_container.removeAllViews()
                val layout2 = inflater.inflate(R.layout.config_prova, layout_container, false)
                layout_container.addView(layout2)
            }
        }

        return rootView
    }
}