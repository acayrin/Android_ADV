package me.acayrin.assignment.activity.fragment.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.fragment.app.Fragment
import me.acayrin.assignment.R
import me.acayrin.assignment.activity.adapter.ClassAdapter
import me.acayrin.assignment.model.ThongTin

class ClassesFragment : Fragment(R.layout.fragment_course_classes) {
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: ClassAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        let {
            val intentFilter = IntentFilter()
            intentFilter.addAction("fpt.class.request.response")
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent()
            intent.action = "fpt.class.request"
            requireContext().sendBroadcast(intent)
        }, 200)

        listView = requireActivity().findViewById(R.id.lv_classes)
        listViewAdapter = ClassAdapter(requireContext())
        listView.apply {
            this.adapter = listViewAdapter
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.class.request.response" -> {
                    Log.i("Fragment::Class", "Received class list from service")

                    listViewAdapter.update(intent.extras!!.getSerializable("list") as ArrayList<ThongTin>)
                }

                else -> {}
            }
        }
    }
}