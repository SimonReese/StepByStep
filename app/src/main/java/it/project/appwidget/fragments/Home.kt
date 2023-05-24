package it.project.appwidget.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.project.appwidget.R

class Home : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragment", "Chiamato onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "Chiamato onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "Chiamato onViewCreated")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d("HomeFragment", "Chiamato onViewStateRestored")
    }

    override fun onStart() {
        super.onStart()
        Log.d("HomeFragment", "Chiamato onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeFragment", "Chiamato onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("HomeFragment", "Chiamato onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("HomeFragment", "Chiamato onStop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("HomeFragment", "Chiamato onSaveInstanceState")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("HomeFragment", "Chiamato onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeFragment", "Chiamato onDestroy")
    }
}