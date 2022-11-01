package me.acayrin.assignment.activity.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import me.acayrin.assignment.R
import me.acayrin.assignment.activity.fragment.component.GreetingFragment

class HomeFragment : Fragment(R.layout.fragment_main_home) {
    override fun onResume() {
        super.onResume()

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.v_fragment_greeting, GreetingFragment())
            .commit()
    }
}