package me.acayrin.assignment.activity.fragment.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import me.acayrin.assignment.R
import me.acayrin.assignment.activity.adapter.CourseAdapter
import me.acayrin.assignment.dao.DAO_DangKy
import me.acayrin.assignment.dao.DAO_MonHoc
import me.acayrin.assignment.dao.DAO_NguoiDung
import me.acayrin.assignment.dao.DAO_ThongTin
import me.acayrin.assignment.model.MonHoc
import me.acayrin.assignment.service.NewsService
import me.acayrin.assignment.service.course.CourseService

class CoursesFragment : Fragment(R.layout.fragment_course_courses) {
    private lateinit var daoNguoidung: DAO_NguoiDung
    private lateinit var daoThongtin: DAO_ThongTin
    private lateinit var daoMonhoc: DAO_MonHoc
    private lateinit var daoDangky: DAO_DangKy
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: CourseAdapter
    private var dialog: dialogBundle? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daoNguoidung = DAO_NguoiDung(requireContext())
        daoThongtin = DAO_ThongTin(requireContext())
        daoMonhoc = DAO_MonHoc(requireContext())
        daoDangky = DAO_DangKy(requireContext())

        // register intent broadcaster
        let {
            val intentFilter = IntentFilter()
            intentFilter.addAction("fpt.course.request.response")
            intentFilter.addAction("fpt.course.register.response")
            intentFilter.addAction("fpt.course.cancel.response")
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent()
            intent.action = "fpt.course.request"
            requireContext().sendBroadcast(intent)
        }, 200)

        val user =
            daoNguoidung[
                    requireContext().getSharedPreferences("fpt_user", Context.MODE_PRIVATE)
                        .getInt("id", -1)
            ]

        listView = requireActivity().findViewById(R.id.lv_courses)
        listViewAdapter = CourseAdapter(requireContext())
        listView.apply {
            this.adapter = listViewAdapter
            this.setOnItemClickListener { _, _, i, _ ->
                if (daoDangky.all.any { dk -> dk.id == user.id && dk.code == i }) {
                    dialog = dialog()
                    dialog!!.let {
                        it.etHead.text = "Cancel enrollment"
                        it.etBody.text = "Please confirm cancel current enrollment to this course"
                        it.dialog.show()
                        it.btnYes.setOnClickListener {
                            val responseIntent = Intent()
                            responseIntent.action = "fpt.course.cancel"
                            responseIntent.putExtra("courseId", i)

                            // clean up and send broadcast
                            requireContext().sendBroadcast(responseIntent)
                        }
                    }
                } else {
                    dialog = dialog()
                    dialog!!.let {
                        it.etHead.text = "Confirm enrollment"
                        it.etBody.text = "Please confirm your enrollment to this course"
                        it.dialog.show()
                        it.btnYes.setOnClickListener {
                            val responseIntent = Intent()
                            responseIntent.action = "fpt.course.register"
                            responseIntent.putExtra("courseId", i)

                            // clean up and send broadcast
                            requireContext().sendBroadcast(responseIntent)
                        }
                    }
                }
            }
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.course.request.response" -> {
                    Log.i("Fragment::Course", "Received course list from service")

                    listViewAdapter.update(intent.extras!!.getSerializable("list") as ArrayList<MonHoc>)
                }

                "fpt.course.register.response" -> {
                    Log.i("Fragment::Course", "Received course registration response from service")

                    if (intent.extras!!.getBoolean("responseStatus")) {
                        Toast.makeText(
                            context,
                            "Successfully joined course",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog?.dialog?.dismiss()
                        dialog = null
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to join course",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                "fpt.course.cancel.response" -> {
                    Log.i("Fragment::Course", "Received course cancellation response from service")

                    if (intent.extras!!.getBoolean("responseStatus")) {
                        Toast.makeText(
                            context,
                            "Successfully left course",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog?.dialog?.dismiss()
                        dialog = null
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to leave course",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {}
            }
        }
    }

    private fun dialog(): dialogBundle {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val alertDialogView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_confirm, null)
        val alertDialog = alertDialogBuilder.setView(alertDialogView).create()

        alertDialogView.findViewById<Button>(R.id.dialog_btn_no).setOnClickListener {
            alertDialog.dismiss()
        }

        return dialogBundle(
            alertDialog,
            alertDialogView.findViewById(R.id.dialog_header),
            alertDialogView.findViewById(R.id.dialog_body),
            alertDialogView.findViewById(R.id.dialog_btn_yes),
            alertDialogView.findViewById(R.id.dialog_btn_no)
        )
    }

    private class dialogBundle(
        val dialog: AlertDialog,
        val etHead: TextView,
        val etBody: TextView,
        val btnYes: Button,
        val btnNo: Button
    )
}