package me.acayrin.assignment.service.course

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import me.acayrin.assignment.dao.*
import me.acayrin.assignment.model.DangKy

class CourseService : Service() {
    private lateinit var daoNguoidung: DAO_NguoiDung
    private lateinit var daoMonhoc: DAO_MonHoc
    private lateinit var daoDangky: DAO_DangKy

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        daoNguoidung = DAO_NguoiDung(this)
        daoMonhoc = DAO_MonHoc(this)
        daoDangky = DAO_DangKy(this)

        val intentFilter = IntentFilter()
        intentFilter.addAction("fpt.course.request")
        intentFilter.addAction("fpt.course.register")
        intentFilter.addAction("fpt.course.cancel")
        registerReceiver(broadcastReceiver, intentFilter)

        Log.i("Service::Course", "Service started")
        return super.onStartCommand(intent, flags, startId)
    }

    // handle requests here
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.course.request" -> {
                    Log.i("Service::Course", "Course list requested")

                    val responseIntent = Intent()
                    val responseBundle = Bundle()
                    responseIntent.action = "fpt.course.request.response"
                    responseBundle.putSerializable("list", daoMonhoc.all)

                    // clean up and send broadcast
                    responseIntent.putExtras(responseBundle)
                    sendBroadcast(responseIntent)
                }

                "fpt.course.register" -> {
                    Log.w("Service::Course", "Course registration requested")

                    val user = daoNguoidung[getSharedPreferences("fpt_user", MODE_PRIVATE).getInt(
                        "id",
                        -1
                    )]
                    val course = daoMonhoc[intent.getIntExtra("courseId", -1)]

                    val responseIntent = Intent()
                    val responseBundle = Bundle()
                    responseIntent.action = "fpt.course.register.response"
                    responseBundle.putBoolean("responseStatus", daoDangky.insert(DangKy(user.id, course.code)))

                    // clean up and send broadcast
                    responseIntent.putExtras(responseBundle)
                    sendBroadcast(responseIntent)
                }

                "fpt.course.cancel" -> {
                    Log.w("Service::Course", "Course cancellation requested")

                    val user = daoNguoidung[getSharedPreferences("fpt_user", MODE_PRIVATE).getInt(
                        "id",
                        -1
                    )]
                    val course = daoMonhoc[intent.getIntExtra("courseId", -1)]

                    val responseIntent = Intent()
                    val responseBundle = Bundle()
                    responseIntent.action = "fpt.course.cancel.response"
                    responseBundle.putBoolean("responseStatus", daoDangky.delete(DangKy(user.id, course.code)))

                    // clean up and send broadcast
                    responseIntent.putExtras(responseBundle)
                    sendBroadcast(responseIntent)
                }
                else -> {

                    // TODO
                    Log.w("Service::Course", "How the fuck did you get here?")
                }
            }
        }
    }
}