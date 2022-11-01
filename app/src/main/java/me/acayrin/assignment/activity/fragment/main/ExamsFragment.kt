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
import me.acayrin.assignment.activity.adapter.ExamAdapter
import me.acayrin.assignment.model.ThongTin

class ExamsFragment : Fragment(R.layout.fragment_course_exams) {
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: ExamAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // register intent broadcaster
        let {
            val intentFilter = IntentFilter()
            intentFilter.addAction("fpt.exam.request.response")
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent()
            intent.action = "fpt.exam.request"
            requireContext().sendBroadcast(intent)
        }, 200)

        listView = requireActivity().findViewById(R.id.lv_exams)
        listViewAdapter = ExamAdapter(requireContext())
        listView.apply {
            this.adapter = listViewAdapter
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.exam.request.response" -> {
                    Log.i("Fragment::Exam", "Received exam list from service")

                    listViewAdapter.update(intent.extras!!.getSerializable("list") as ArrayList<ThongTin>)
                }

                else -> {}
            }
        }
    }
}