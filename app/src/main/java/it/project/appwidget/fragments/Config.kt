package it.project.appwidget.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Switch
import androidx.navigation.fragment.NavHostFragment
import it.project.appwidget.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Config : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var navigationHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.config_prova, container, false)

        val switchBtn = rootView.findViewById<Switch>(R.id.switch_btn)

        //prelevo il frame layout
        val layout_container = rootView.findViewById<FrameLayout>(R.id.frame_layout)

        /**
         * switchBtn.setOnCheckedChangeListener { _, isChecked ->
        Log.i("Switch", "dentro la funzione")
        if(isChecked){
        Log.i("Switch", "IsChecked = true")
        //mostro layout 1
        layout_container.removeAllViews()
        val layout1 = inflater.inflate(R.layout.config_prova, layout_container, false)
        layout_container.addView(layout1)
        }else {
        Log.i("Switch", "IsChecked = false")
        //switch è disattivo
        layout_container.removeAllViews()
        val layout2 = inflater.inflate(R.layout.fragment_setup, layout_container, false)
        layout_container.addView(layout2)
        }
        }
         */

        switchBtn.setOnClickListener {
            if(switchBtn.isChecked){
                Log.i("Switch", "IsChecked = true")
                layout_container.removeAllViews()
                val layout1 = inflater.inflate(R.layout.fragment_setup, layout_container, false)
                layout_container.addView(layout1)
            }else {
                Log.i("Switch", "IsChecked = false")
                //switch è disattivo
                layout_container.removeAllViews()
                val layout2 = inflater.inflate(R.layout.fragment_setup, layout_container, false)
                layout_container.addView(layout2)
            }
        }


        return rootView

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Config.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Config().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}