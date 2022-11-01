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
import me.acayrin.assignment.model.MonHoc
import me.acayrin.assignment.model.ThongTin

class ClassService : Service() {
    private lateinit var daoDangky: DAO_DangKy
    private lateinit var daoMonhoc: DAO_MonHoc
    private lateinit var daoThongtin: DAO_ThongTin

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        daoDangky = DAO_DangKy(this)
        daoMonhoc = DAO_MonHoc(this)
        daoThongtin = DAO_ThongTin(this)

        val intentFilter = IntentFilter()
        intentFilter.addAction("fpt.class.request")
        registerReceiver(broadcastReceiver, intentFilter)

        Log.i("Service::Class", "Service started")
        return super.onStartCommand(intent, flags, startId)
    }

    // handle requests here
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.class.request" -> {
                    Log.i("Service::Class", "Class list requested")

                    val courseList = ArrayList<MonHoc>()
                    daoDangky.all.takeWhile {
                        it.id == getSharedPreferences("fpt_user", MODE_PRIVATE).getInt("id", -1)
                    }.forEach {
                        courseList.add(daoMonhoc[it.code])
                    }
                    val classList =
                        daoThongtin.all.filter { tt -> courseList.any { cc -> cc.code == tt.code } }

                    /**
                     * ThongTin - code
                     * MonHoc - code
                     * DangKy - id, code
                     * NguoiDung - id
                     */

                    val responseIntent = Intent()
                    val responseBundle = Bundle()
                    responseIntent.action = "fpt.class.request.response"
                    responseBundle.putSerializable(
                        "list",
                        ArrayList(classList)
                    )

                    // clean up and send broadcast
                    responseIntent.putExtras(responseBundle)
                    sendBroadcast(responseIntent)
                }
                else -> {

                    // TODO
                    Log.w("Service::Class", "How the fuck did you get here?")
                }
            }
        }
    }
}