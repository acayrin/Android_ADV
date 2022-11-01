package me.acayrin.assignment.activity.fragment.component

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import me.acayrin.assignment.R

class GreetingFragment : Fragment(R.layout.component_greeting) {
    private var tvUsername: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (tvUsername == null) tvUsername =
            activity?.findViewById(R.id.v_fragment_greeting_username)
        
        tvUsername?.text = requireContext()
            .getSharedPreferences("fpt_user", Context.MODE_PRIVATE)
            .getString("username", "unknown")
    }
}