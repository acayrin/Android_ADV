package me.acayrin.assignment.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import me.acayrin.assignment.dao.DAO_Base
import me.acayrin.assignment.dao.DAO_NguoiDung
import me.acayrin.assignment.model.NguoiDung

class AccountService : Service() {
    private lateinit var daoNguoiDung: DAO_NguoiDung

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        daoNguoiDung = DAO_NguoiDung(this)

        val intentFilter = IntentFilter()
        intentFilter.addAction("fpt.account.request")
        intentFilter.addAction("fpt.account.register")
        registerReceiver(broadcastReceiver, intentFilter)

        Log.w("Service::Account", "Service started")
        return super.onStartCommand(intent, flags, startId)
    }

    // handle requests here
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.account.request" -> {
                    Log.w("Service::Account", "Account access requested")

                    val bundle = intent.extras as Bundle
                    val inUsername = bundle.getString("inUsername")
                    val inPassword = bundle.getString("inPassword")

                    val responseIntent = Intent()
                    val responseBundle = Bundle()
                    responseIntent.action = "fpt.account.response"

                    // if either username or password is empty
                    if (inUsername!!.isEmpty() || inPassword!!.isEmpty()) {
                        responseBundle.putBoolean("responseStatus", false)
                        responseBundle.putString("responseMessage", "Invalid input")

                        // if user verification succeed
                    } else if (daoNguoiDung.verify(NguoiDung(0, null, inUsername, inPassword))) {
                        responseBundle.putBoolean("responseStatus", true)
                        responseBundle.putString(
                            "responseUsername",
                            inUsername
                        )
                        responseBundle.putInt(
                            "responseUserId",
                            daoNguoiDung.all.takeWhile { nd -> nd.username == inUsername }[0].id
                        )
                        responseBundle.putString("responseMessage", "Success")

                        // if user verification failed
                    } else {
                        responseBundle.putBoolean("responseStatus", false)
                        responseBundle.putString(
                            "responseMessage",
                            "Incorrect username or password"
                        )
                    }

                    // clean up and send broadcast
                    responseIntent.putExtras(responseBundle)
                    sendBroadcast(responseIntent)
                }
                "fpt.account.register" -> {
                    Log.w("Service::Account", "Account registration requested")

                    val bundle = intent.extras as Bundle
                    val inFullname = bundle.getString("inFullname")
                    val inUsername = bundle.getString("inUsername")
                    val inPassword = bundle.getString("inPassword")

                    val responseIntent = Intent()
                    val responseBundle = Bundle()
                    responseIntent.action = "fpt.account.response"

                    // if either username or password is empty
                    if (inFullname!!.isEmpty() || inUsername!!.isEmpty() || inPassword!!.isEmpty()) {
                        responseBundle.putBoolean("responseStatus", false)
                        responseBundle.putString("responseMessage", "Invalid input")

                        // if user verification succeed
                    } else if (daoNguoiDung.doWork(
                            NguoiDung(
                                daoNguoiDung.all.size,
                                inFullname,
                                inUsername,
                                inPassword
                            ), DAO_Base.Work.INSERT
                        )
                    ) {
                        responseBundle.putBoolean("responseStatus", true)
                        responseBundle.putString("responseMessage", "Success")

                        // if user verification failed
                    } else {
                        responseBundle.putBoolean("responseStatus", false)
                        responseBundle.putString("responseMessage", "Failed to create account")
                    }

                    // clean up and send broadcast
                    responseIntent.putExtras(responseBundle)
                    sendBroadcast(responseIntent)
                }
                else -> {

                    // TODO
                    Log.w("Service::Account", "How the fuck did you get here?")
                }
            }
        }
    }
}